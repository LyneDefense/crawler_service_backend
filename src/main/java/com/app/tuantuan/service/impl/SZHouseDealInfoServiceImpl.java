package com.app.tuantuan.service.impl;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.crawler.SZHouseDealInfoCrawler;
import com.app.tuantuan.model.dto.housedeal.SZHouseDealsInfoDto;
import com.app.tuantuan.repository.SZHouseDealsInfoRepository;
import com.app.tuantuan.service.ISZHouseDealInfoService;
import java.io.IOException;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SZHouseDealInfoServiceImpl implements ISZHouseDealInfoService {

  @Resource SZHouseDealsInfoRepository houseDealsInfoRepository;
  @Resource SZHouseDealInfoCrawler houseDealInfoCrawler;

  @Override
  public List<SZHouseDealsInfoDto> getTodayHouseDealsInfo(int year, int month) {
    return houseDealsInfoRepository.selectLatestHouseDealsInfoByDate(year, month);
  }

  @Override
  public void crawlAndSaveTodayHouseDealsInfo() {
    try {
      List<SZHouseDealsInfoDto> dtos = houseDealInfoCrawler.crawl();
      houseDealsInfoRepository.saveHouseDealsInfo(dtos);
    } catch (IOException e) {
      throw new CustomException("爬取并保存商品房成交信息失败");
    }
  }
}
