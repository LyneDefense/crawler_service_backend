package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.mapper.SZHouseDealsDetailMapper;
import com.app.tuantuan.mapper.SZHouseDealInfoMapper;
import com.app.tuantuan.mapper.HouseDealsAreaDetailMapper;
import com.app.tuantuan.model.dto.housedeal.SZHouseDealsInfoDto;
import com.app.tuantuan.model.entity.housedeal.SZHouseDealsArealDetailDO;
import com.app.tuantuan.model.entity.housedeal.SZHouseDealsDetailDO;
import com.app.tuantuan.model.entity.housedeal.SZHouseDealsInfoDO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SZHouseDealsInfoRepository {

  @Resource
  SZHouseDealInfoMapper SZHouseDealInfoMapper;
  @Resource
  SZHouseDealsDetailMapper SZHouseDealsDetailMapper;
  @Resource HouseDealsAreaDetailMapper houseDealsAreaDetailMapper;

  @Transactional
  public void saveHouseDealsInfo(List<SZHouseDealsInfoDto> szHouseDealsInfoDtos) {
    if (Objects.isNull(szHouseDealsInfoDtos) || szHouseDealsInfoDtos.isEmpty()) {
      return;
    }
    LocalDate today = LocalDate.now();
    Map<LocalDate, List<SZHouseDealsInfoDto>> dateGroup =
        szHouseDealsInfoDtos.stream()
            .collect(Collectors.groupingBy(SZHouseDealsInfoDto::getUpdateDate));
    for (Map.Entry<LocalDate, List<SZHouseDealsInfoDto>> entry : dateGroup.entrySet()) {
      List<SZHouseDealsInfoDto> list = entry.getValue();
      SZHouseDealsInfoDto element = list.get(0);
      this.deleteHouseDealsInfoByYearMonth(element.getYear(), element.getMonth(), today);
      for (SZHouseDealsInfoDto dto : list) {
        SZHouseDealsInfoDO szHouseDealsInfoDO = dto.to();
        log.info(
            "[保存商品房成交信息,所在城市:深圳,所在区:{},日期:{}]", dto.getDistrict().getValue(), dto.getUpdateDate());
        int i = SZHouseDealInfoMapper.insert(szHouseDealsInfoDO);
        if (i != 1) {
          throw new CustomException("保存商品房成交信息失败");
        }
        String id = szHouseDealsInfoDO.getId();
        List<SZHouseDealsDetailDO> houseDealsDetailDOS =
            dto.getDealsDetails().stream().map(e -> e.to(id)).toList();
        log.info("[保存商品房成交详情,数量:{}]", houseDealsDetailDOS.size());
        SZHouseDealsDetailMapper.insertBatch(houseDealsDetailDOS);
        List<SZHouseDealsArealDetailDO> szHouseDealsArealDetailDOS =
            dto.getDealsAreaDetails().stream().map(e -> e.to(id)).toList();
        log.info("[保存商品房成交面积详情,数量:{}]", szHouseDealsArealDetailDOS.size());
        houseDealsAreaDetailMapper.insertBatch(szHouseDealsArealDetailDOS);
      }
    }

    log.info("[保存商品房成交信息成功]");
  }

  public List<SZHouseDealsInfoDto> selectLatestHouseDealsInfoByDate(int year, int month) {
    List<SZHouseDealsInfoDO> szHouseDealsInfoDOS = selectLatestByYearMonth(year, month);
    if (szHouseDealsInfoDOS.isEmpty()) {
      return new ArrayList<>();
    }
    List<SZHouseDealsInfoDto> szHouseDealsInfoDtos = new ArrayList<>();
    for (SZHouseDealsInfoDO szHouseDealsInfoDO : szHouseDealsInfoDOS) {
      List<SZHouseDealsDetailDO> houseDealsDetailDOS =
          SZHouseDealsDetailMapper.selectList(
              new LambdaQueryWrapperX<SZHouseDealsDetailDO>()
                  .eq(SZHouseDealsDetailDO::getParentId, szHouseDealsInfoDO.getId()));
      List<SZHouseDealsArealDetailDO> SZHouseDealsArealDetailDOS =
          houseDealsAreaDetailMapper.selectList(
              new LambdaQueryWrapperX<SZHouseDealsArealDetailDO>()
                  .eq(SZHouseDealsArealDetailDO::getParentId, szHouseDealsInfoDO.getId()));
      szHouseDealsInfoDtos.add(
          SZHouseDealsInfoDto.of(
              szHouseDealsInfoDO, houseDealsDetailDOS, SZHouseDealsArealDetailDOS));
    }
    return szHouseDealsInfoDtos;
  }

  @Transactional
  public void deleteHouseDealsInfoByYearMonth(int year, int month, LocalDate updateDate) {
    List<SZHouseDealsInfoDO> szHouseDealsInfoDOS =
        this.selectByYearMonthAndUpdateDate(year, month, updateDate);
    if (szHouseDealsInfoDOS.isEmpty()) {
      log.info("[{}年{}月,更新日期:{},商品房成交信息不存在，无需删除]", year, month, updateDate);
      return;
    }
    for (SZHouseDealsInfoDO szHouseDealsInfoDO : szHouseDealsInfoDOS) {
      SZDistrictEnum districtEnum = szHouseDealsInfoDO.getDistrict();
      log.info(
          "[{}年{}月,更新日期:{},城市:深圳,区位:{},商品房成交信息存在，准备进行删除]",
          year,
          month,
          updateDate,
          districtEnum.getValue());
      String id = szHouseDealsInfoDO.getId();
      SZHouseDealInfoMapper.deleteById(id);
      SZHouseDealsDetailMapper.delete(
          new LambdaQueryWrapperX<SZHouseDealsDetailDO>()
              .eq(SZHouseDealsDetailDO::getParentId, id));
      houseDealsAreaDetailMapper.delete(
          new LambdaQueryWrapperX<SZHouseDealsArealDetailDO>()
              .eq(SZHouseDealsArealDetailDO::getParentId, id));
      log.info("[{}年{}月,城市:深圳,区位:{},商品房成交信息删除成功]", year, month, districtEnum.getValue());
    }
    log.info("[{}年{}月,更新日期:{},城市:深圳,商品房成交信息删除成功]", year, month, updateDate);
  }

  public List<SZHouseDealsInfoDO> selectLatestByYearMonth(int year, int month) {
    List<SZHouseDealsInfoDO> list =
        SZHouseDealInfoMapper.selectList(
            new LambdaQueryWrapperX<SZHouseDealsInfoDO>()
                .eq(SZHouseDealsInfoDO::getYear, year)
                .eq(SZHouseDealsInfoDO::getMonth, month)
                .orderByDesc(SZHouseDealsInfoDO::getUpdateDate));
    if (list.isEmpty()) {
      return new ArrayList<>();
    }
    return list.stream()
        .collect(Collectors.groupingBy(SZHouseDealsInfoDO::getUpdateDate))
        .entrySet()
        .stream()
        .max(Map.Entry.comparingByKey())
        .map(Map.Entry::getValue)
        .orElse(List.of());
  }

  public List<SZHouseDealsInfoDO> selectByYearMonthAndUpdateDate(
      int year, int month, LocalDate updateDate) {
    return SZHouseDealInfoMapper.selectList(
        new LambdaQueryWrapperX<SZHouseDealsInfoDO>()
            .eq(SZHouseDealsInfoDO::getYear, year)
            .eq(SZHouseDealsInfoDO::getMonth, month)
            .eq(SZHouseDealsInfoDO::getUpdateDate, updateDate)
            .orderByDesc(SZHouseDealsInfoDO::getUpdateDate));
  }
}
