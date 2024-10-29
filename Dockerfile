# 使用官方 OpenJDK 17 作为基础镜像
FROM openjdk:17-jdk-alpine

# 设置应用程序的工作目录
WORKDIR /app

# 复制 Maven 构建的 jar 文件到容器中
# 假设你的 jar 文件位于 target 目录下，文件名为 app.jar
COPY target/*.jar app.jar

# 暴露应用程序运行的端口（这里是 8082）
EXPOSE 8082

# 运行应用程序
ENTRYPOINT ["java","-jar","app.jar"]
