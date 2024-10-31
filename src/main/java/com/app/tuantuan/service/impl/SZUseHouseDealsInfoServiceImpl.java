package com.app.tuantuan.service.impl;

import com.app.tuantuan.crawler.SZUsedHouseDealsCrawler;
import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsInfoDto;
import com.app.tuantuan.repository.SZUsedHouseDealsInfoRepository;
import com.app.tuantuan.service.ISZUseHouseDealsInfoService;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.stereotype.Service;

@Service
public class SZUseHouseDealsInfoServiceImpl implements ISZUseHouseDealsInfoService {

  @Resource SZUsedHouseDealsInfoRepository usedHouseDealsInfoRepository;
  @Resource SZUsedHouseDealsCrawler usedHouseDealsCrawler;

  @Override
  public void crawlAndSaveUseHouseDealsInfo() {
    List<SZUsedHouseDealsInfoDto> usedHouseDealsInfoDtos = usedHouseDealsCrawler.crawl();
    usedHouseDealsInfoRepository.saveUsedHouseDealsInfo(usedHouseDealsInfoDtos);
  }

  @Override
  public List<SZUsedHouseDealsInfoDto> selectUsedHouseDealsInfoByDate(LocalDate date) {
    return usedHouseDealsInfoRepository.selectUsedHouseDealsInfoByDate(date);
  }
}
