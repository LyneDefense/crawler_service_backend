package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.mapper.*;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignDetailDto;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignInfoDto;
import com.app.tuantuan.model.dto.onlinesign.presale.SZSubscriptionOnlineSignInfoDto;
import com.app.tuantuan.model.entity.onlinesign.onsale.SZOnsaleContractOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.onsale.SZOnsaleContractOnlineSignInfoDO;
import com.app.tuantuan.model.entity.onlinesign.preale.SZContractOnlineSignAreaDetailDO;
import com.app.tuantuan.model.entity.onlinesign.preale.SZContractOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.preale.SZSubscriptionOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.preale.SZSubscriptionOnlineSignInfoDO;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SZSubscriptionOnlineSignInfoRepository {

  @Resource private SubscriptionOnlineSignInfoMapper subscriptionOnlineSignInfoMapper;
  @Resource private ContractOnlineSignAreaDetailMapper contractOnlineSignAreaDetailMapper;
  @Resource private SubscriptionOnlineSignDetailMapper subscriptionOnlineSignDetailMapper;
  @Resource private ContractOnlineSignDetailMapper contractOnlineSignDetailMapper;
  @Resource private OnsaleContractOnlineSignDetailMapper onsaleContractOnlineSignDetailMapper;
  @Resource private OnsaleContractOnlineSignInfoMapper onsaleContractOnlineSignInfoMapper;

  /**
   * 保存预售网签信息
   *
   * @param onlineSignInfoDtos 预售网签信息
   */
  @Transactional
  public void saveSubscriptionOnlineSignInfo(
      List<SZSubscriptionOnlineSignInfoDto> onlineSignInfoDtos) {
    if (onlineSignInfoDtos == null || onlineSignInfoDtos.isEmpty()) {
      return;
    }
    LocalDate date = onlineSignInfoDtos.get(0).getDate();
    this.deleteSubscriptionOnlineSignInfoByDate(date);
    for (SZSubscriptionOnlineSignInfoDto onlineSignInfoDto : onlineSignInfoDtos) {
      SZSubscriptionOnlineSignInfoDO subscriptionOnlineSignInfoDO = onlineSignInfoDto.to();
      log.info(
          "[保存深圳“预售”商品房网签认购信息,district:{},date:{}]",
          onlineSignInfoDto.getDistrict().getValue(),
          date);
      int i = subscriptionOnlineSignInfoMapper.insert(subscriptionOnlineSignInfoDO);
      if (i != 1) {
        throw new CustomException("[保存深圳“预售”商品房网签认购信息失败]");
      }
      String parentId = subscriptionOnlineSignInfoDO.getId();

      List<SZContractOnlineSignAreaDetailDO> contractOnlineSignAreaDetailDOS =
          onlineSignInfoDto.getContractOnlineSignAreaDetails().stream()
              .map(e -> e.to(parentId))
              .toList();
      log.info("[保存“预售”商品住房按面积统计购房合同网签信息,数量:{}]", contractOnlineSignAreaDetailDOS.size());
      contractOnlineSignAreaDetailMapper.insertBatch(contractOnlineSignAreaDetailDOS);

      List<SZSubscriptionOnlineSignDetailDO> subscriptionOnlineSignDetailDOS =
          onlineSignInfoDto.getSubscriptionDetails().stream().map(e -> e.to(parentId)).toList();
      log.info("[保存“预售”认购网签信息,数量:{}]", subscriptionOnlineSignDetailDOS.size());
      subscriptionOnlineSignDetailMapper.insertBatch(subscriptionOnlineSignDetailDOS);

      List<SZContractOnlineSignDetailDO> contractOnlineSignDetailDOS =
          onlineSignInfoDto.getContractOnlineSignDetails().stream()
              .map(e -> e.to(parentId))
              .toList();
      log.info("[保存“预售”商品房购房合同网签信息,数量:{}]", contractOnlineSignDetailDOS.size());
      contractOnlineSignDetailMapper.insertBatch(contractOnlineSignDetailDOS);
    }
  }

  /**
   * 保存现售网签信息
   *
   * @param onlineSignInfoDtos 现售网签信息
   */
  @Transactional
  public void saveSubscriptionOnSaleOnlineSignInfo(
      List<SZOnsaleContractOnlineSignInfoDto> onlineSignInfoDtos) {
    if (onlineSignInfoDtos == null || onlineSignInfoDtos.isEmpty()) {
      return;
    }
    LocalDate date = onlineSignInfoDtos.get(0).getDate();
    this.deleteOnSaleSubscriptionOnlineSignInfoByDate(date);
    for (SZOnsaleContractOnlineSignInfoDto onlineSignInfoDto : onlineSignInfoDtos) {
      SZOnsaleContractOnlineSignInfoDO onsaleContractOnlineSignInfoDO = onlineSignInfoDto.to();
      log.info(
          "[保存深圳“现售”商品房网签认购信息,district:{},date:{}]",
          onlineSignInfoDto.getDistrict().getValue(),
          date);
      int i = onsaleContractOnlineSignInfoMapper.insert(onsaleContractOnlineSignInfoDO);
      if (i != 1) {
        throw new CustomException("[保存深圳“现售”商品房网签认购信息失败]");
      }
      String parentId = onsaleContractOnlineSignInfoDO.getId();

      List<SZOnsaleContractOnlineSignDetailDO> onsaleContractOnlineSignDetailDOS =
          onlineSignInfoDto.getOnsaleDetails().stream().map(e -> e.to(parentId)).toList();
      log.info("[保存“现售”商品房购房合同网签信息,数量:{}]", onsaleContractOnlineSignDetailDOS.size());
      onsaleContractOnlineSignDetailMapper.insertBatch(onsaleContractOnlineSignDetailDOS);
    }
  }

  /**
   * 按时间获取预售网签信息
   *
   * @param date date
   * @return 预售网签信息
   */
  public List<SZSubscriptionOnlineSignInfoDto> selectSubscriptionOnlineSignInfoByDate(
      LocalDate date) {
    List<SZSubscriptionOnlineSignInfoDto> subscriptionOnlineSignInfoDtos = new ArrayList<>();
    List<SZSubscriptionOnlineSignInfoDO> subscriptionOnlineSignInfoDOS =
        subscriptionOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZSubscriptionOnlineSignInfoDO>()
                .eq(SZSubscriptionOnlineSignInfoDO::getDate, date));
    return getSzSubscriptionOnlineSignInfoDtos(
        subscriptionOnlineSignInfoDtos, subscriptionOnlineSignInfoDOS);
  }

  public List<SZSubscriptionOnlineSignInfoDto> selectSubscriptionOnlineSignInfoByDatePeriod(
      LocalDate starDate, LocalDate endDate) {
    List<SZSubscriptionOnlineSignInfoDto> subscriptionOnlineSignInfoDtos = new ArrayList<>();
    List<SZSubscriptionOnlineSignInfoDO> subscriptionOnlineSignInfoDOS =
        subscriptionOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZSubscriptionOnlineSignInfoDO>()
                .between(SZSubscriptionOnlineSignInfoDO::getDate, starDate, endDate));
    return getSzSubscriptionOnlineSignInfoDtos(
        subscriptionOnlineSignInfoDtos, subscriptionOnlineSignInfoDOS);
  }

  private List<SZSubscriptionOnlineSignInfoDto> getSzSubscriptionOnlineSignInfoDtos(
      List<SZSubscriptionOnlineSignInfoDto> subscriptionOnlineSignInfoDtos,
      List<SZSubscriptionOnlineSignInfoDO> subscriptionOnlineSignInfoDOS) {
    for (SZSubscriptionOnlineSignInfoDO subscriptionOnlineSignInfoDO :
        subscriptionOnlineSignInfoDOS) {
      String parentId = subscriptionOnlineSignInfoDO.getId();
      List<SZContractOnlineSignAreaDetailDO> contractOnlineSignAreaDetailDOS =
          contractOnlineSignAreaDetailMapper.selectList(
              new LambdaQueryWrapperX<SZContractOnlineSignAreaDetailDO>()
                  .eq(SZContractOnlineSignAreaDetailDO::getParentId, parentId));
      List<SZSubscriptionOnlineSignDetailDO> subscriptionOnlineSignDetailDOS =
          subscriptionOnlineSignDetailMapper.selectList(
              new LambdaQueryWrapperX<SZSubscriptionOnlineSignDetailDO>()
                  .eq(SZSubscriptionOnlineSignDetailDO::getParentId, parentId));
      List<SZContractOnlineSignDetailDO> contractOnlineSignDetailDOS =
          contractOnlineSignDetailMapper.selectList(
              new LambdaQueryWrapperX<SZContractOnlineSignDetailDO>()
                  .eq(SZContractOnlineSignDetailDO::getParentId, parentId));
      subscriptionOnlineSignInfoDtos.add(
          SZSubscriptionOnlineSignInfoDto.of(
              subscriptionOnlineSignInfoDO,
              subscriptionOnlineSignDetailDOS,
              contractOnlineSignDetailDOS,
              contractOnlineSignAreaDetailDOS));
    }
    return subscriptionOnlineSignInfoDtos;
  }

  /**
   * 按时间获取现售网签信息
   *
   * @param date date
   * @return 现售网签信息
   */
  public List<SZOnsaleContractOnlineSignInfoDto> selectOnSaleSubscriptionOnlineSignInfoByDate(
      LocalDate date) {
    List<SZOnsaleContractOnlineSignInfoDto> onsaleContractOnlineSignInfoDtos = new ArrayList<>();
    List<SZOnsaleContractOnlineSignInfoDO> onsaleContractOnlineSignInfoDOS =
        onsaleContractOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZOnsaleContractOnlineSignInfoDO>()
                .eq(SZOnsaleContractOnlineSignInfoDO::getDate, date));
    for (SZOnsaleContractOnlineSignInfoDO onsaleContractOnlineSignInfoDO :
        onsaleContractOnlineSignInfoDOS) {
      String parentId = onsaleContractOnlineSignInfoDO.getId();
      List<SZOnsaleContractOnlineSignDetailDO> onsaleContractOnlineSignDetailDOS =
          onsaleContractOnlineSignDetailMapper.selectList(
              new LambdaQueryWrapperX<SZOnsaleContractOnlineSignDetailDO>()
                  .eq(SZOnsaleContractOnlineSignDetailDO::getParentId, parentId));
      onsaleContractOnlineSignInfoDtos.add(
          SZOnsaleContractOnlineSignInfoDto.of(
              onsaleContractOnlineSignInfoDO,
              onsaleContractOnlineSignDetailDOS.stream()
                  .map(SZOnsaleContractOnlineSignDetailDto::of)
                  .toList()));
    }
    return onsaleContractOnlineSignInfoDtos;
  }

  /**
   * 按时间删除预售网签信息
   *
   * @param date date
   */
  @Transactional
  public void deleteSubscriptionOnlineSignInfoByDate(LocalDate date) {
    List<SZSubscriptionOnlineSignInfoDO> subscriptionOnlineSignInfoDOS =
        subscriptionOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZSubscriptionOnlineSignInfoDO>()
                .eq(SZSubscriptionOnlineSignInfoDO::getDate, date));
    if (subscriptionOnlineSignInfoDOS == null || subscriptionOnlineSignInfoDOS.isEmpty()) {
      log.info("[没有找到深圳商品房“预售”网签认购信息,date:{}]", date);
      return;
    }
    for (SZSubscriptionOnlineSignInfoDO subscriptionOnlineSignInfoDO :
        subscriptionOnlineSignInfoDOS) {
      String parentId = subscriptionOnlineSignInfoDO.getId();
      subscriptionOnlineSignInfoMapper.deleteById(parentId);
      log.info("[删除深圳商品房“预售”网签认购信息成功,date:{},id:{}]", date, parentId);
      int i =
          contractOnlineSignAreaDetailMapper.delete(
              new LambdaQueryWrapperX<SZContractOnlineSignAreaDetailDO>()
                  .eq(SZContractOnlineSignAreaDetailDO::getParentId, parentId));
      log.info("[删除商品住房“预售”按面积统计购房合同网签信息成功,date:{},parentId:{},共删除{}条记录]", date, parentId, i);
      int j =
          subscriptionOnlineSignDetailMapper.delete(
              new LambdaQueryWrapperX<SZSubscriptionOnlineSignDetailDO>()
                  .eq(SZSubscriptionOnlineSignDetailDO::getParentId, parentId));
      log.info("[删除“预售”认购网签信息成功,date:{},parentId:{}，共删除{}条记录]", date, parentId, j);
      int k =
          contractOnlineSignDetailMapper.delete(
              new LambdaQueryWrapperX<SZContractOnlineSignDetailDO>()
                  .eq(SZContractOnlineSignDetailDO::getParentId, parentId));
      log.info("[删除“预售”商品房购房合同网签信息成功,date:{},parentId:{}，共删除{}条记录]", date, parentId, k);
    }
  }

  /**
   * 按时间删除现售网签信息
   *
   * @param date date
   */
  @Transactional
  public void deleteOnSaleSubscriptionOnlineSignInfoByDate(LocalDate date) {
    List<SZOnsaleContractOnlineSignInfoDO> onsaleSignDOs =
        onsaleContractOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZOnsaleContractOnlineSignInfoDO>()
                .eq(SZOnsaleContractOnlineSignInfoDO::getDate, date));
    if (onsaleSignDOs == null || onsaleSignDOs.isEmpty()) {
      log.info("[没有找到深圳商品房“现售”网签认购信息,date:{}]", date);
      return;
    }
    for (SZOnsaleContractOnlineSignInfoDO onsaleSignDO : onsaleSignDOs) {
      String parentId = onsaleSignDO.getId();
      onsaleContractOnlineSignInfoMapper.deleteById(parentId);
      log.info("[删除深圳商品房“现售”网签认购信息成功,date:{},id:{}]", date, parentId);
      int i =
          onsaleContractOnlineSignDetailMapper.delete(
              new LambdaQueryWrapperX<SZOnsaleContractOnlineSignDetailDO>()
                  .eq(SZOnsaleContractOnlineSignDetailDO::getParentId, parentId));
      log.info("[删除“现售”商品房购房合同网签信息成功,date:{},parentId:{}，共删除{}条记录]", date, parentId, i);
    }
  }
}
