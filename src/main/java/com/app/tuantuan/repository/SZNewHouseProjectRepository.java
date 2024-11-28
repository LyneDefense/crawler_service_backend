package com.app.tuantuan.repository;

import com.app.tuantuan.enumeration.CrawlStatus;
import com.app.tuantuan.model.dto.newhouse.*;
import java.util.*;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SZNewHouseProjectRepository {

  @Resource SZNewHouseMainPageRepository newHouseMainPageRepository;
  @Resource SZNewHouseSalesBuildingRepository newHouseSalesBuildingRepository;
  @Resource SZNewHouseSalesInfoRepository szNewHouseSalesInfoRepository;
  @Resource SZNewHouseBuildingInfoRepository newHouseBuildingInfoRepository;

  @Transactional
  public void saveAllNewHouseInfoByMainPageItem(
      NewHouseMainPageItemDto maiPageItem,
      NewHouseSalesMixedInfoDto mixedSalesInfo,
      List<NewHouseBuildingInfoDto> newHouseBuildingInfoDtos) {
    // 先查看mainPageItem对应的预售证号是否存在，如果存在则删除
    NewHouseMainPageItemDto existMainPageItemDO =
        newHouseMainPageRepository.selectMainPageItemByPreSaleNumber(
            maiPageItem.getPreSaleNumber());
    if (existMainPageItemDO == null) {
      log.info(
          "[不存在预售证号为:{},项目名称为:{},的一手房源公示首页信息,跳过删除]",
          maiPageItem.getPreSaleNumber(),
          maiPageItem.getProjectName());
    } else {
      // 删除首页信息项
      newHouseMainPageRepository.deleteMainPageItem(
          existMainPageItemDO.to(existMainPageItemDO.getId()));
      // 删除相关的'房源项目卖方信息-楼栋信息'
      newHouseSalesBuildingRepository.deleteNewHouseSalesBuildingInfoByProjectId(
          existMainPageItemDO.getId());
      // 删除相关的'房源项目卖方信息'
      szNewHouseSalesInfoRepository.deleteNewHouseSalesInfos(existMainPageItemDO.getId());
      // 删除相关楼栋详情信息
      newHouseBuildingInfoRepository.deleteNewHouseBuildingInfo(existMainPageItemDO.getId());
    }
    NewHouseMainPageItemDto updated = newHouseMainPageRepository.saveMainPageItem(maiPageItem);
    newHouseSalesBuildingRepository.saveNewHouseSalesBuildingInfo(
        mixedSalesInfo.getBuildingInfos(), updated);
    szNewHouseSalesInfoRepository.saveNewHouseSalesInfos(mixedSalesInfo.getSalesInfo(), updated);
    newHouseBuildingInfoRepository.saveNewHouseBuildingInfo(newHouseBuildingInfoDtos, updated);
  }

  public List<SZNewHouseProjectDto> selectNewHouseProjectByMainPageItems(
      List<NewHouseMainPageItemDto> mainPageItemDtos) {
    List<SZNewHouseProjectDto> projectDtos = new ArrayList<>();
    for (NewHouseMainPageItemDto mainPageItemDto : mainPageItemDtos) {
      List<NewHouseBuildingInfoDto> buildings =
          newHouseBuildingInfoRepository.selectNewHouseBuildingInfo(mainPageItemDto.getId());
      Optional<NewHouseSalesInfoDto> optionalNewHouseSalesInfoDto =
          szNewHouseSalesInfoRepository.selectNewHouseSalesInfos(mainPageItemDto.getId());
      NewHouseSalesInfoDto salesInfo = optionalNewHouseSalesInfoDto.orElse(null);
      projectDtos.add(
          SZNewHouseProjectDto.builder()
              .status(CrawlStatus.SUCCESS)
              .projectName(mainPageItemDto.getProjectName())
              .mainPageItem(mainPageItemDto)
              .buildings(buildings)
              .salesInfo(salesInfo)
              .build());
    }
    return projectDtos;
  }
}
