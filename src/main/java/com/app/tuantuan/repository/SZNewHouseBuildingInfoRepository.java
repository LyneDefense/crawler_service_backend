package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.mapper.NewHouseBlockInfoMapper;
import com.app.tuantuan.mapper.NewHouseBuildingInfoMapper;
import com.app.tuantuan.mapper.NewHouseUnitInfoMapper;
import com.app.tuantuan.model.dto.newhouse.NewHouseBlockInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseBuildingInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.entity.newhouse.NewHouseBlockInfoDO;
import com.app.tuantuan.model.entity.newhouse.NewHouseBuildingInfoDO;
import com.app.tuantuan.model.entity.newhouse.NewHouseUnitInfoDO;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SZNewHouseBuildingInfoRepository {

  @Resource NewHouseBuildingInfoMapper newHouseBuildingInfoMapper;
  @Resource NewHouseBlockInfoMapper newHouseBlockInfoMapper;
  @Resource NewHouseUnitInfoMapper newHouseUnitInfoMapper;

  public List<NewHouseBuildingInfoDto> selectNewHouseBuildingInfo(String projectId) {

    List<NewHouseBuildingInfoDO> buildingInfoDOS =
        newHouseBuildingInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseBuildingInfoDO>()
                .eq(NewHouseBuildingInfoDO::getProjectId, projectId));

    if (buildingInfoDOS.isEmpty()) {
      log.warn("[projectId: {} 对应的楼栋信息为空]", projectId);
      return Collections.emptyList();
    }

    List<String> buildingIds =
        buildingInfoDOS.stream().map(NewHouseBuildingInfoDO::getId).collect(Collectors.toList());

    // 获取所有区块信息
    List<NewHouseBlockInfoDO> blockInfoDOS =
        newHouseBlockInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseBlockInfoDO>()
                .in(NewHouseBlockInfoDO::getBuildingId, buildingIds));

    if (blockInfoDOS.isEmpty()) {
      log.warn("[projectId: {} 对应的区块信息为空]", projectId);
      return Collections.emptyList();
    }

    List<String> blockIds =
        blockInfoDOS.stream().map(NewHouseBlockInfoDO::getId).collect(Collectors.toList());

    // 获取所有单元信息
    List<NewHouseUnitInfoDO> unitInfoDOS =
        newHouseUnitInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseUnitInfoDO>()
                .in(NewHouseUnitInfoDO::getBlockId, blockIds));

    Map<String, List<NewHouseBlockInfoDO>> buildingToBlocksMap =
        blockInfoDOS.stream().collect(Collectors.groupingBy(NewHouseBlockInfoDO::getBuildingId));

    Map<String, List<NewHouseUnitInfoDO>> blockToUnitsMap =
        unitInfoDOS.stream().collect(Collectors.groupingBy(NewHouseUnitInfoDO::getBlockId));

    // 组装 DTO 列表
    return buildingInfoDOS.stream()
        .map(
            buildingDO -> {
              List<NewHouseBlockInfoDO> blocks =
                  buildingToBlocksMap.getOrDefault(buildingDO.getId(), Collections.emptyList());

              // 收集当前建筑下所有区块的单元
              List<NewHouseUnitInfoDO> unitsForBuilding =
                  blocks.stream()
                      .flatMap(
                          block ->
                              blockToUnitsMap
                                  .getOrDefault(block.getId(), Collections.emptyList())
                                  .stream())
                      .collect(Collectors.toList());

              return NewHouseBuildingInfoDto.of(buildingDO, blocks, unitsForBuilding);
            })
        .collect(Collectors.toList());
  }

  @Transactional
  public void saveNewHouseBuildingInfo(
      List<NewHouseBuildingInfoDto> dtos, NewHouseMainPageItemDto maiPageItem) {
    for (NewHouseBuildingInfoDto dto : dtos) {
      log.info(
          "[保存房源'项目卖方信息-楼栋-楼栋详情'信息,项目名称:{},楼栋名称:{}]",
          maiPageItem.getProjectName(),
          dto.getBuildingName());
      // 保存建筑信息
      NewHouseBuildingInfoDO buildingInfoDO = dto.to(null, maiPageItem.getId());
      newHouseBuildingInfoMapper.insert(buildingInfoDO);
      // 保存区块和单元信息
      List<NewHouseUnitInfoDO> unitInfoDOS = new ArrayList<>();
      for (NewHouseBlockInfoDto blockInfoDto : dto.getBlockInfoList()) {
        // 保存区块信息
        NewHouseBlockInfoDO blockInfoDO = blockInfoDto.to(null, buildingInfoDO.getId());
        newHouseBlockInfoMapper.insert(blockInfoDO);
        // 保存单元信息
        blockInfoDto
            .getUnitInfoList()
            .forEach(
                unitInfoDto -> {
                  unitInfoDOS.add(unitInfoDto.to(null, blockInfoDO.getId()));
                });
      }
      // 批量插入单元信息
      if (!unitInfoDOS.isEmpty()) {
        newHouseUnitInfoMapper.insertBatch(unitInfoDOS);
      }
    }
  }

  @Transactional
  public void deleteNewHouseBuildingInfo(String projectId) {
    // 查询所有相关的建筑信息
    List<NewHouseBuildingInfoDO> buildingInfoDOS =
        newHouseBuildingInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseBuildingInfoDO>()
                .eq(NewHouseBuildingInfoDO::getProjectId, projectId));

    if (buildingInfoDOS.isEmpty()) {
      log.info("[删除'项目卖方信息-楼栋-楼栋详情',未找到 projectId: {} 的楼栋信息, 跳过删除", projectId);
      return;
    }
    List<String> buildingIds =
        buildingInfoDOS.stream().map(NewHouseBuildingInfoDO::getId).collect(Collectors.toList());

    List<NewHouseBlockInfoDO> blockInfoDOS =
        newHouseBlockInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseBlockInfoDO>()
                .in(NewHouseBlockInfoDO::getBuildingId, buildingIds));
    List<String> blockIds =
        blockInfoDOS.stream().map(NewHouseBlockInfoDO::getId).collect(Collectors.toList());

    List<NewHouseUnitInfoDO> unitInfoDOS =
        newHouseUnitInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseUnitInfoDO>()
                .in(NewHouseUnitInfoDO::getBlockId, blockIds));

    List<String> unitIds =
        unitInfoDOS.stream().map(NewHouseUnitInfoDO::getId).collect(Collectors.toList());
    if (!unitIds.isEmpty()) {
      int deletedUnits = newHouseUnitInfoMapper.deleteBatchIds(unitIds);
      log.info("[删除单元信息 找到: {} 条记录, 删除: {} 条记录]", unitIds.size(), deletedUnits);
    }
    if (!blockIds.isEmpty()) {
      int deletedBlocks = newHouseBlockInfoMapper.deleteBatchIds(blockIds);
      log.info("[删除区块信息 找到: {} 条记录, 删除: {} 条记录]", blockIds.size(), deletedBlocks);
    }
    int deletedBuildings = newHouseBuildingInfoMapper.deleteBatchIds(buildingIds);
    log.info("[删除楼栋信息 找到: {} 条记录, 删除: {} 条记录]", buildingIds.size(), deletedBuildings);
  }
}
