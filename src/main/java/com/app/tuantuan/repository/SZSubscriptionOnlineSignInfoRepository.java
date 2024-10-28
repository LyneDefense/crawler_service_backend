package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.mapper.ContractOnlineSignAreaDetailMapper;
import com.app.tuantuan.mapper.ContractOnlineSignDetailMapper;
import com.app.tuantuan.mapper.SubscriptionOnlineSignDetailMapper;
import com.app.tuantuan.mapper.SubscriptionOnlineSignInfoMapper;
import com.app.tuantuan.model.dto.onlinesign.SZContractOnlineSignAreaDetailDto;
import com.app.tuantuan.model.dto.onlinesign.SZContractOnlineSignDetailDto;
import com.app.tuantuan.model.dto.onlinesign.SZSubscriptionOnlineSignDetailDto;
import com.app.tuantuan.model.dto.onlinesign.SZSubscriptionOnlineSignInfoDto;
import com.app.tuantuan.model.entity.onlinesign.SZContractOnlineSignAreaDetailDO;
import com.app.tuantuan.model.entity.onlinesign.SZContractOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.SZSubscriptionOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.SZSubscriptionOnlineSignInfoDO;
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
          "[保存深圳商品房网签认购信息,district:{},date:{}]", onlineSignInfoDto.getDistrict().getValue(), date);
      int i = subscriptionOnlineSignInfoMapper.insert(subscriptionOnlineSignInfoDO);
      if (i != 1) {
        log.error("[保存深圳商品房网签认购信息失败]");
        return;
      }
      String parentId = subscriptionOnlineSignInfoDO.getId();

      List<SZContractOnlineSignAreaDetailDO> contractOnlineSignAreaDetailDOS =
          onlineSignInfoDto.getContractOnlineSignAreaDetails().stream()
              .map(e -> e.to(parentId))
              .toList();
      log.info("[保存商品住房按面积统计购房合同网签信息,数量:{}]", contractOnlineSignAreaDetailDOS.size());
      contractOnlineSignAreaDetailMapper.insertBatch(contractOnlineSignAreaDetailDOS);

      List<SZSubscriptionOnlineSignDetailDO> subscriptionOnlineSignDetailDOS =
          onlineSignInfoDto.getSubscriptionDetails().stream().map(e -> e.to(parentId)).toList();
      log.info("[保存认购网签信息,数量:{}]", subscriptionOnlineSignDetailDOS.size());
      subscriptionOnlineSignDetailMapper.insertBatch(subscriptionOnlineSignDetailDOS);

      List<SZContractOnlineSignDetailDO> contractOnlineSignDetailDOS =
          onlineSignInfoDto.getContractOnlineSignDetails().stream()
              .map(e -> e.to(parentId))
              .toList();
      log.info("[保存商品房购房合同网签信息,数量:{}]", contractOnlineSignDetailDOS.size());
      contractOnlineSignDetailMapper.insertBatch(contractOnlineSignDetailDOS);
    }
  }

  public List<SZSubscriptionOnlineSignInfoDto> selectSubscriptionOnlineSignInfoByDate(
      LocalDate date) {
    List<SZSubscriptionOnlineSignInfoDto> subscriptionOnlineSignInfoDtos = new ArrayList<>();
    List<SZSubscriptionOnlineSignInfoDO> subscriptionOnlineSignInfoDOS =
        subscriptionOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZSubscriptionOnlineSignInfoDO>()
                .eq(SZSubscriptionOnlineSignInfoDO::getDate, date));
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

  @Transactional
  public void deleteSubscriptionOnlineSignInfoByDate(LocalDate date) {
    List<SZSubscriptionOnlineSignInfoDO> subscriptionOnlineSignInfoDOS =
        subscriptionOnlineSignInfoMapper.selectList(
            new LambdaQueryWrapperX<SZSubscriptionOnlineSignInfoDO>()
                .eq(SZSubscriptionOnlineSignInfoDO::getDate, date));
    if (subscriptionOnlineSignInfoDOS == null || subscriptionOnlineSignInfoDOS.isEmpty()) {
      log.info("[没有找到深圳商品房网签认购信息,date:{}]", date);
      return;
    }
    for (SZSubscriptionOnlineSignInfoDO subscriptionOnlineSignInfoDO :
        subscriptionOnlineSignInfoDOS) {
      String parentId = subscriptionOnlineSignInfoDO.getId();
      subscriptionOnlineSignInfoMapper.deleteById(parentId);
      log.info("[删除深圳商品房网签认购信息成功,date:{},id:{}]", date, parentId);
      int i =
          contractOnlineSignAreaDetailMapper.delete(
              new LambdaQueryWrapperX<SZContractOnlineSignAreaDetailDO>()
                  .eq(SZContractOnlineSignAreaDetailDO::getParentId, parentId));
      log.info("[删除商品住房按面积统计购房合同网签信息成功,date:{},parentId:{},共删除{}条记录]", date, parentId, i);
      int j =
          subscriptionOnlineSignDetailMapper.delete(
              new LambdaQueryWrapperX<SZSubscriptionOnlineSignDetailDO>()
                  .eq(SZSubscriptionOnlineSignDetailDO::getParentId, parentId));
      log.info("[删除认购网签信息成功,date:{},parentId:{}，共删除{}条记录]", date, parentId, j);
      int k =
          contractOnlineSignDetailMapper.delete(
              new LambdaQueryWrapperX<SZContractOnlineSignDetailDO>()
                  .eq(SZContractOnlineSignDetailDO::getParentId, parentId));
      log.info("[删除商品房购房合同网签信息成功,date:{},parentId:{}，共删除{}条记录]", date, parentId, k);
    }
  }
}
