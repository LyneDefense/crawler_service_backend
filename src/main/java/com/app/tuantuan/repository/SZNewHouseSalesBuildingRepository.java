package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.mapper.NewHouseSalesBuildingInfoMapper;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseSalesBuildingInfoDto;
import com.app.tuantuan.model.entity.newhouse.NewHouseSalesBuildingInfoDO;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
public class SZNewHouseSalesBuildingRepository {

  @Resource NewHouseSalesBuildingInfoMapper newHouseSalesBuildingInfoMapper;

  public List<NewHouseSalesBuildingInfoDto> selectNewHouseSalesBuildingInfos(String projectId) {
    List<NewHouseSalesBuildingInfoDO> entities =
        newHouseSalesBuildingInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseSalesBuildingInfoDO>()
                .eq(NewHouseSalesBuildingInfoDO::getProjectId, projectId));
    return entities.stream().map(NewHouseSalesBuildingInfoDto::of).toList();
  }

  @Transactional
  public void saveNewHouseSalesBuildingInfo(
      List<NewHouseSalesBuildingInfoDto> dtos, @NotNull NewHouseMainPageItemDto maiPageItem) {
    log.info("[保存房源'项目卖方信息-楼栋信息',项目名称:{}]", maiPageItem.getProjectName());
    List<NewHouseSalesBuildingInfoDO> newHouseSalesBuildingInfoDOS =
        dtos.stream().map(e -> e.to(null, maiPageItem.getId())).toList();
    newHouseSalesBuildingInfoMapper.insertBatch(newHouseSalesBuildingInfoDOS);
  }

  public void deleteNewHouseSalesBuildingInfoByProjectId(String projectId) {
    List<NewHouseSalesBuildingInfoDO> entities =
        newHouseSalesBuildingInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseSalesBuildingInfoDO>()
                .eq(NewHouseSalesBuildingInfoDO::getProjectId, projectId));
    if (entities.isEmpty()) {
      log.info("[删除-未找到'房源项目卖方信息-楼栋信息',projectId为: {} 的楼栋信息,跳过删除]", projectId);
      return;
    }
    int i =
        newHouseSalesBuildingInfoMapper.deleteBatchIds(
            entities.stream().map(NewHouseSalesBuildingInfoDO::getId).toList());
    log.info(
        "[删除'房源项目卖方信息-楼栋信息',projectId为: {} 的楼栋信息成功,找到:{}条记录,共删除:{} 条记录]",
        projectId,
        entities.size(),
        i);
  }

  public void deleteBatchNewHouseSalesBuildingInfoByProjectId(List<String> projectIds) {
    int i =
        newHouseSalesBuildingInfoMapper.delete(
            new LambdaQueryWrapperX<NewHouseSalesBuildingInfoDO>()
                .inIfPresent(NewHouseSalesBuildingInfoDO::getProjectId, projectIds));
    log.info("[批量删除'房源项目卖方信息-楼栋信息'成功,找到:{}条记录,共删除:{} 条记录]", projectIds.size(), i);
  }
}
