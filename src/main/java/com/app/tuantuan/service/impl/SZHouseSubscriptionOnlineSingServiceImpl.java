package com.app.tuantuan.service.impl;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.crawler.SZHouseOnlineSignInfoCrawler;
import com.app.tuantuan.crawler.SZOnsaleHouseOnlineSignInfoCrawler;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignInfoDto;
import com.app.tuantuan.model.dto.onlinesign.presale.SZSubscriptionOnlineSignInfoDto;
import com.app.tuantuan.repository.SZSubscriptionOnlineSignInfoRepository;
import com.app.tuantuan.service.ISZHouseSubscriptionOnlineSingService;
import java.io.IOException;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SZHouseSubscriptionOnlineSingServiceImpl
    implements ISZHouseSubscriptionOnlineSingService {

  @Resource private SZSubscriptionOnlineSignInfoRepository subscriptionOnlineSignInfoRepository;

  @Resource private SZHouseOnlineSignInfoCrawler onlineSignInfoCrawler;

  @Resource private SZOnsaleHouseOnlineSignInfoCrawler onsaleHouseOnlineSignInfoCrawler;

  @Override
  public List<SZSubscriptionOnlineSignInfoDto> selectSubscriptionOnlineSignInfoByDate(
      LocalDate date) {
    return subscriptionOnlineSignInfoRepository.selectSubscriptionOnlineSignInfoByDate(date);
  }

  @Override
  public void crawAndSaveSubscriptionOnSaleOnlineSignInfo() {
    try {
      List<SZOnsaleContractOnlineSignInfoDto> onlineSignInfoDtos =
          onsaleHouseOnlineSignInfoCrawler.crawl();
      subscriptionOnlineSignInfoRepository.saveSubscriptionOnSaleOnlineSignInfo(onlineSignInfoDtos);
    } catch (Exception e) {
      throw new CustomException("爬取并保存深圳商品房“现售”网签认购信息失败");
    }
  }

  @Override
  public void crawAndSaveSubscriptionOnlineSignInfo() {
    try {
      List<SZSubscriptionOnlineSignInfoDto> dtos = onlineSignInfoCrawler.crawl();
      subscriptionOnlineSignInfoRepository.saveSubscriptionOnlineSignInfo(dtos);
    } catch (IOException e) {
      throw new CustomException("爬取并保存深圳商品房网“预售”签认购信息失败");
    }
  }

  @Override
  public List<SZOnsaleContractOnlineSignInfoDto> selectOnSaleSubscriptionOnlineSignInfoByDate(
          LocalDate date) {
    return subscriptionOnlineSignInfoRepository.selectOnSaleSubscriptionOnlineSignInfoByDate(date);
  }
}
