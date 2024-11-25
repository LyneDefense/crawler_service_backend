#!/bin/bash

# =============================================================================
# 脚本名称: build_and_transfer.sh
# 功能描述: 构建 Docker 镜像、保存为 tar 文件、计算哈希并上传到服务器
# 使用方法: ./build_and_transfer.sh
# =============================================================================

# ----------------------------- 配置部分 -------------------------------------

# Docker 镜像名称和标签
IMAGE_NAME="lyneee/crawler-service-backend:latest"

# 镜像保存路径
LOCAL_SAVE_DIR="/Users/pinjiehu/docker_images"
IMAGE_FILE="${LOCAL_SAVE_DIR}/crawler-service-backend_latest.tar"
HASH_FILE="${LOCAL_SAVE_DIR}/crawler-service-backend_latest.tar.sha256"

# 服务器信息
SERVER_USER="lighthouse"
SERVER_IP="114.132.41.21"
SERVER_IMAGE_DIR="/home/lighthouse/docker_images"
SERVER_HASH_FILE="${SERVER_IMAGE_DIR}/crawler-service-backend_latest.tar.sha256_server_computed"

# 日志文件路径
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
LOG_FILE="${SCRIPT_DIR}/build_and_transfer.log"

# ----------------------------- 函数部分 -------------------------------------

# 函数: 记录日志
# 参数:
#   $1 - 日志级别 (INFO, ERROR)
#   $2 - 日志信息
log() {
    echo "$(date '+%Y-%m-%d %H:%M:%S') [$1] $2" | tee -a "$LOG_FILE"
}

# 函数: 传输文件并验证哈希
transfer_and_verify() {
    local src_file="$1"
    local dest_dir="$2"

    # 传输镜像文件并显示实时进度
    log "INFO" "正在传输镜像文件 '$src_file' 到服务器..."
    {
        rsync -av --progress "$src_file" "${SERVER_USER}@${SERVER_IP}:${dest_dir}/" 2>&1
    } | tee -a "$LOG_FILE"
    if [ ${PIPESTATUS[0]} -ne 0 ]; then
        log "ERROR" "镜像文件传输失败。"
        exit 1
    fi
    log "INFO" "镜像文件传输成功。"

    # 传输本地哈希文件
    log "INFO" "正在传输哈希文件 '$HASH_FILE' 到服务器..."
    {
        rsync -av --progress "$HASH_FILE" "${SERVER_USER}@${SERVER_IP}:${dest_dir}/crawler-service-backend_latest.tar.sha256" 2>&1
    } | tee -a "$LOG_FILE"
    if [ ${PIPESTATUS[0]} -ne 0 ]; then
        log "ERROR" "哈希文件传输失败。"
        exit 1
    fi
    log "INFO" "哈希文件传输成功。"

    # 在服务器上计算并比较哈希值
    log "INFO" "在服务器上验证镜像文件的完整性..."
    ssh "${SERVER_USER}@${SERVER_IP}" "
        shasum -a 256 ${dest_dir}/crawler-service-backend_latest.tar | awk '{print \$1}' > ${dest_dir}/crawler-service-backend_latest.tar.sha256_computed
        diff ${dest_dir}/crawler-service-backend_latest.tar.sha256 ${dest_dir}/crawler-service-backend_latest.tar.sha256_computed
    " | tee -a "$LOG_FILE"

    if [ $? -eq 0 ]; then
        log "INFO" "镜像文件传输完整。"
    else
        log "ERROR" "镜像文件传输不完整或已损坏。请重新运行脚本。"
        exit 1
    fi
}

# ----------------------------- 脚本执行部分 ---------------------------------

# 记录脚本开始
log "INFO" "==================== 构建并上传 Docker 镜像开始 ===================="

# 检查 Docker 是否安装
if ! command -v docker &> /dev/null; then
    log "ERROR" "Docker 未安装。请先安装 Docker 并重试。"
    exit 1
fi
log "INFO" "Docker 已安装。"

# 构建 Docker 镜像
log "INFO" "正在构建 Docker 镜像 '$IMAGE_NAME'..."
docker build -t "$IMAGE_NAME" .
if [ $? -ne 0 ]; then
    log "ERROR" "Docker 镜像构建失败。"
    exit 1
fi
log "INFO" "Docker 镜像构建成功。"

# 保存 Docker 镜像为 tar 文件
log "INFO" "正在保存 Docker 镜像为 tar 文件 '$IMAGE_FILE'..."
mkdir -p "$LOCAL_SAVE_DIR"
docker save "$IMAGE_NAME" -o "$IMAGE_FILE"
if [ $? -ne 0 ]; then
    log "ERROR" "Docker 镜像保存失败。"
    exit 1
fi
log "INFO" "Docker 镜像保存成功。"

# 计算本地镜像文件的哈希值（仅哈希值，不包含路径）
log "INFO" "正在计算镜像文件的哈希值..."
shasum -a 256 "$IMAGE_FILE" | awk '{print $1}' > "$HASH_FILE"
if [ $? -ne 0 ]; then
    log "ERROR" "计算哈希值失败。"
    exit 1
fi
log "INFO" "哈希值计算成功。"

# 传输并验证镜像文件
transfer_and_verify "$IMAGE_FILE" "$SERVER_IMAGE_DIR"

# 部署完成
log "INFO" "==================== 构建并上传 Docker 镜像完成 ===================="

exit 0
