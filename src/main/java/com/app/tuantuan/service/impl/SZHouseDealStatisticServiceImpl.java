package com.app.tuantuan.service.impl;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.crawler.SZHouseDealsStatisticDataCrawler;
import com.app.tuantuan.enumeration.DateFormat;
import com.app.tuantuan.mapper.SZHouseDealStatisticDataMapper;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticDataDto;
import com.app.tuantuan.model.entity.statistic.SZHouseDealStatisticDataDO;
import com.app.tuantuan.service.ISZHouseDealStatisticService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
    log.info(
        "[开始保存爬取的数据到数据库中，日期区间:{}-{},共{}条]", startDate, defaultEndDate, statisticDataDOS.size());
    dealStatisticDataMapper.insertBatch(statisticDataDOS);
  }

  @Override
  public List<SZHouseDealStatisticDataDto> findHouseDealStatisticByDate(
      LocalDate startDate, LocalDate endDate, DateFormat dateFormat) {

    if (startDate.isAfter(endDate)) {
      return new ArrayList<>();
    }

    // 从数据库中查询数据
    List<SZHouseDealStatisticDataDO> dataList =
        dealStatisticDataMapper
            .selectList(
                new LambdaQueryWrapperX<SZHouseDealStatisticDataDO>()
                    .betweenIfPresent(SZHouseDealStatisticDataDO::getDate, startDate, endDate)
                    .orderByDesc(SZHouseDealStatisticDataDO::getDate))
            .stream()
            .toList();

    // 转换为 DTO
    List<SZHouseDealStatisticDataDto> dtoList =
        dataList.stream().map(SZHouseDealStatisticDataDto::of).toList();

    if (dateFormat == null || dateFormat == DateFormat.DAY) {
      // 按天返回
      return dtoList.stream()
          .sorted(Comparator.comparing(SZHouseDealStatisticDataDto::getDate).reversed())
          .collect(Collectors.toList());
    } else if (dateFormat == DateFormat.MONTH) {
      // 按月返回
      return dtoList.stream()
          .collect(Collectors.groupingBy(dto -> dto.getDate().withDayOfMonth(1)))
          .entrySet()
          .stream()
          .sorted(
              Map.Entry.<LocalDate, List<SZHouseDealStatisticDataDto>>comparingByKey().reversed())
          .map(entry -> aggregateData(entry.getValue(), entry.getKey()))
          .collect(Collectors.toList());
    } else if (dateFormat == DateFormat.YEAR) {
      // 按年返回
      return dtoList.stream()
          .collect(Collectors.groupingBy(dto -> dto.getDate().withDayOfYear(1)))
          .entrySet()
          .stream()
          .sorted(
              Map.Entry.<LocalDate, List<SZHouseDealStatisticDataDto>>comparingByKey().reversed())
          .map(entry -> aggregateData(entry.getValue(), entry.getKey()))
          .collect(Collectors.toList());
    } else {
      return new ArrayList<>();
    }
  }

  /**
   * 聚合一组数据到一个 DTO 对象。
   *
   * @param dtoList 需要聚合的 DTO 列表
   * @param aggregateDate 聚合后的日期（按月或按年）
   * @return 聚合后的 DTO 对象
   */
  private SZHouseDealStatisticDataDto aggregateData(
      List<SZHouseDealStatisticDataDto> dtoList, LocalDate aggregateDate) {
    SZHouseDealStatisticDataDto aggregated = new SZHouseDealStatisticDataDto();
    aggregated.setDate(aggregateDate); // 设置为聚合后的日期（月份或年份的第一天）

    // 聚合字段：求和
    double totalNewHouseDealArea =
        dtoList.stream().mapToDouble(SZHouseDealStatisticDataDto::getNewHouseDealArea).sum();
    int totalNewHouseDealSetCount =
        dtoList.stream().mapToInt(SZHouseDealStatisticDataDto::getNewHouseDealSetCount).sum();
    double totalUsedHouseDealArea =
        dtoList.stream().mapToDouble(SZHouseDealStatisticDataDto::getUsedHouseDealArea).sum();
    int totalUsedHouseDealCount =
        dtoList.stream().mapToInt(SZHouseDealStatisticDataDto::getUsedHouseDealCount).sum();

    aggregated.setNewHouseDealArea(totalNewHouseDealArea);
    aggregated.setNewHouseDealSetCount(totalNewHouseDealSetCount);
    aggregated.setUsedHouseDealArea(totalUsedHouseDealArea);
    aggregated.setUsedHouseDealCount(totalUsedHouseDealCount);

    return aggregated;
  }
}
