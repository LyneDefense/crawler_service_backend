package com.app.tuantuan.service.impl;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.crawler.SZHouseDealsStatisticDataCrawler;
import com.app.tuantuan.mapper.SZHouseDealStatisticDataMapper;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticDataDto;
import com.app.tuantuan.model.entity.statistic.SZHouseDealStatisticDataDO;
import com.app.tuantuan.service.ISZHouseDealStatisticService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SZHouseDealStatisticServiceImpl implements ISZHouseDealStatisticService {

  @Resource SZHouseDealStatisticDataMapper dealStatisticDataMapper;
  @Resource SZHouseDealsStatisticDataCrawler houseDealsDataCrawler;

  @Override
  public void crawlAndSaveStatisticData() {

    LocalDate defaultStartDate = LocalDate.of(2007, 1, 1);
    LocalDate defaultEndDate = LocalDate.now();
    LocalDate startDate;
    LocalDate maxDate = dealStatisticDataMapper.findMaxDate();
    List<SZHouseDealStatisticDataDto> statisticDataDtos = new ArrayList<>();
    if (maxDate == null) {
      log.info("[原始表中不存在任何记录，从默认日期开始爬取数据，开始日期:{}，结束日期:{}]", defaultStartDate, defaultEndDate);
      startDate = defaultStartDate;
      statisticDataDtos = houseDealsDataCrawler.crawl(startDate, defaultEndDate);
    } else {
      if (maxDate.isEqual(defaultEndDate) || maxDate.isAfter(defaultEndDate)) {
        log.info("[原始表中数据已是最新，查询到的最大日期:{}，不需要爬取数据]", maxDate);
        return;
      }
      startDate = maxDate.plusDays(1);
      log.info(
          "[原始表中数据不是最新，从最大日期后一天开始爬取数据，数据库中最大日期:{},开始日期:{}，结束日期:{}]",
          maxDate,
          startDate,
          defaultEndDate);
      statisticDataDtos = houseDealsDataCrawler.crawl(startDate, defaultEndDate);
    }
    List<SZHouseDealStatisticDataDO> statisticDataDOS =
        statisticDataDtos.stream().map(e -> e.to(null)).toList();
    log.info("[开始保存爬取的数据到数据库中，日期区间:{}-{},共{}条]", startDate, defaultEndDate, statisticDataDOS.size());
    dealStatisticDataMapper.insertBatch(statisticDataDOS);
  }

  @Override
  public List<SZHouseDealStatisticDataDto> findHouseDealStatisticByDate(
      LocalDate startDate, LocalDate endDate) {
    if (startDate.isAfter(endDate)) {
      return new ArrayList<>();
    }
    return dealStatisticDataMapper
        .selectList(
            new LambdaQueryWrapperX<SZHouseDealStatisticDataDO>()
                .betweenIfPresent(SZHouseDealStatisticDataDO::getDate, startDate, endDate)
                .orderByDesc(SZHouseDealStatisticDataDO::getDate))
        .stream()
        .map(SZHouseDealStatisticDataDto::of)
        .toList();
  }
}
