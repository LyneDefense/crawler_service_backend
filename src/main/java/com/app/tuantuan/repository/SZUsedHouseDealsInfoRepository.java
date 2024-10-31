package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.mapper.SZUsedHouseDealsDetailMapper;
import com.app.tuantuan.mapper.SZUsedHouseDealsInfoMapper;
import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsDetailDto;
import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsInfoDto;
import com.app.tuantuan.model.entity.housedeal.used.SZUsedHouseDealsDetailDO;
import com.app.tuantuan.model.entity.housedeal.used.SZUsedHouseDealsInfoDO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SZUsedHouseDealsInfoRepository {

  @Resource SZUsedHouseDealsInfoMapper dealsInfoMapper;

  @Resource SZUsedHouseDealsDetailMapper dealsDetailMapper;

  @Transactional
  public void saveUsedHouseDealsInfo(List<SZUsedHouseDealsInfoDto> usedHouseDealsInfoDtos) {
    if (usedHouseDealsInfoDtos == null || usedHouseDealsInfoDtos.isEmpty()) {
      return;
    }
    LocalDate date = usedHouseDealsInfoDtos.get(0).getDate();
    this.deleteUsedHouseDealsInfoByDate(date);
    for (SZUsedHouseDealsInfoDto usedHouseDealsInfoDto : usedHouseDealsInfoDtos) {
      SZUsedHouseDealsInfoDO usedHouseDealsInfoDO = usedHouseDealsInfoDto.to();
      log.info("[保存二手商品住房成交信息,区域:{},日期:{}]", usedHouseDealsInfoDO.getDistrict().getValue(), date);
      int i = dealsInfoMapper.insert(usedHouseDealsInfoDO);
      if (i != 1) {
        throw new CustomException("[保存二手商品住房成交信息失败]");
      }
      String parentId = usedHouseDealsInfoDO.getId();
      List<SZUsedHouseDealsDetailDO> usedHouseDealsDetailDOS =
          usedHouseDealsInfoDto.getDetails().stream().map(e -> e.to(parentId)).toList();
      log.info("[保存二手商品住房成交详情,数量:{}]", usedHouseDealsDetailDOS.size());
      dealsDetailMapper.insertBatch(usedHouseDealsDetailDOS);
    }
  }

  public List<SZUsedHouseDealsInfoDto> selectUsedHouseDealsInfoByDate(LocalDate date) {
    List<SZUsedHouseDealsInfoDto> usedHouseDealsInfoDtos = new ArrayList<>();
    List<SZUsedHouseDealsInfoDO> usedHouseDealsInfoDOS =
        dealsInfoMapper.selectList(
            new LambdaQueryWrapperX<SZUsedHouseDealsInfoDO>()
                .eq(SZUsedHouseDealsInfoDO::getDate, date));
    if (usedHouseDealsInfoDOS.isEmpty()) {
      return new ArrayList<>();
    }
    for (SZUsedHouseDealsInfoDO usedHouseDealsInfoDO : usedHouseDealsInfoDOS) {
      String parentId = usedHouseDealsInfoDO.getId();
      List<SZUsedHouseDealsDetailDO> usedHouseDealsDetailDOS =
          dealsDetailMapper.selectList(
              new LambdaQueryWrapperX<SZUsedHouseDealsDetailDO>()
                  .eq(SZUsedHouseDealsDetailDO::getParentId, parentId));
      List<SZUsedHouseDealsDetailDto> usedHouseDealsDetailDtos =
          usedHouseDealsDetailDOS.stream().map(SZUsedHouseDealsDetailDto::of).toList();
      usedHouseDealsInfoDtos.add(
          SZUsedHouseDealsInfoDto.of(usedHouseDealsInfoDO, usedHouseDealsDetailDtos));
    }
    return usedHouseDealsInfoDtos;
  }

  @Transactional
  public void deleteUsedHouseDealsInfoByDate(LocalDate date) {
    List<SZUsedHouseDealsInfoDO> usedHouseDealsInfoDOS =
        dealsInfoMapper.selectList(
            new LambdaQueryWrapperX<SZUsedHouseDealsInfoDO>()
                .eq(SZUsedHouseDealsInfoDO::getDate, date));
    if (usedHouseDealsInfoDOS.isEmpty()) {
      log.info("[没有找到需要删除的二手商品住房成交信息,date:{}]", date);
      return;
    }
    for (SZUsedHouseDealsInfoDO usedHouseDealsInfoDO : usedHouseDealsInfoDOS) {
      log.info("[删除二手商品住房成交信息,日期:{}]", usedHouseDealsInfoDO.getDate());
      String parentId = usedHouseDealsInfoDO.getId();
      dealsInfoMapper.deleteById(parentId);
      int i =
          dealsDetailMapper.delete(
              new LambdaQueryWrapperX<SZUsedHouseDealsDetailDO>()
                  .eq(SZUsedHouseDealsDetailDO::getParentId, parentId));
      log.info("[删除二手商品住房成交详情,parentId:{},日期:{},数量:{}]", parentId, date, i);
    }
  }
}
