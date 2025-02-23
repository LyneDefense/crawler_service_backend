package com.app.tuantuan.service.impl;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.crawler.SZHouseDealsStatisticDataCrawler;
import com.app.tuantuan.enumeration.DateFormat;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.mapper.SZHouseDealStatisticDataMapper;
import com.app.tuantuan.model.dto.onlinesign.presale.SZSubscriptionOnlineSignDetailDto;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticDataDto;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticIntegrationDto;
import com.app.tuantuan.model.entity.statistic.SZHouseDealStatisticDataDO;
import com.app.tuantuan.repository.SZSubscriptionOnlineSignInfoRepository;
import com.app.tuantuan.service.ISZHouseDealStatisticService;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SZHouseDealStatisticServiceImpl implements ISZHouseDealStatisticService {

  @Resource SZHouseDealStatisticDataMapper dealStatisticDataMapper;
  @Resource SZHouseDealsStatisticDataCrawler houseDealsDataCrawler;
  @Resource SZSubscriptionOnlineSignInfoRepository subscriptionOnlineSignInfoRepository;

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
      LocalDate startDate, LocalDate endDate) {

    if (startDate.isAfter(endDate)) {
      return new ArrayList<>();
    }

    // 从数据库中查询数据
    return dealStatisticDataMapper
        .selectList(
            new LambdaQueryWrapperX<SZHouseDealStatisticDataDO>()
                .betweenIfPresent(SZHouseDealStatisticDataDO::getDate, startDate, endDate)
                .orderByDesc(SZHouseDealStatisticDataDO::getDate))
        .stream()
        .map(SZHouseDealStatisticDataDto::of)
        .toList();
  }

  @Override
  public List<SZHouseDealStatisticIntegrationDto> findHouseDealStatisticIntegrationByDate(
      LocalDate startDate, LocalDate endDate, DateFormat dateFormat) {

    // 查询房屋交易统计数据并转换为DTO，同时构建日期到DTO的映射
    Map<LocalDate, SZHouseDealStatisticDataDto> dealStatisticDataDtoMap =
        dealStatisticDataMapper
            .selectList(
                new LambdaQueryWrapperX<SZHouseDealStatisticDataDO>()
                    .betweenIfPresent(SZHouseDealStatisticDataDO::getDate, startDate, endDate)
                    .orderByDesc(SZHouseDealStatisticDataDO::getDate))
            .stream()
            .map(SZHouseDealStatisticDataDto::of)
            .collect(Collectors.toMap(SZHouseDealStatisticDataDto::getDate, Function.identity()));

    // 查询订阅在线签约信息并构建日期到详细信息的映射
    Map<LocalDate, SZSubscriptionOnlineSignDetailDto> subscriptionOnlineSignDetailDtoMap =
        subscriptionOnlineSignInfoRepository
            .selectSubscriptionOnlineSignInfoByDatePeriod(startDate, endDate)
            .stream()
            .filter(e -> e.getDistrict() == SZDistrictEnum.ALL)
            .flatMap(
                dto ->
                    dto.getSubscriptionDetails().stream()
                        .filter(SZSubscriptionOnlineSignDetailDto::isResidenceOnlineSign)
                        .map(detailDto -> new AbstractMap.SimpleEntry<>(dto.getDate(), detailDto)))
            .collect(
                Collectors.toMap(
                    Map.Entry::getKey, Map.Entry::getValue, (existing, replacement) -> existing));

    // 合并交易数据和订阅数据
    List<SZHouseDealStatisticIntegrationDto> integratedData =
        dealStatisticDataDtoMap.entrySet().stream()
            .map(
                entry -> {
                  LocalDate date = entry.getKey();
                  SZHouseDealStatisticDataDto dealData = entry.getValue();
                  SZSubscriptionOnlineSignDetailDto subscriptionData =
                      subscriptionOnlineSignDetailDtoMap.get(date);

                  return SZHouseDealStatisticIntegrationDto.builder()
                      .date(date)
                      .newHouseDealArea(dealData.getNewHouseDealArea())
                      .newHouseDealSetCount(dealData.getNewHouseDealSetCount())
                      .usedHouseDealArea(dealData.getUsedHouseDealArea())
                      .usedHouseDealCount(dealData.getUsedHouseDealCount())
                      .onlineSubscriptionArea(
                          subscriptionData != null ? subscriptionData.getSubscriptionArea() : null)
                      .onlineSubscriptionCount(
                          subscriptionData != null ? subscriptionData.getSubscriptionCount() : null)
                      .build();
                })
            .toList();

    Comparator<LocalDate> localDateComparator = Comparator.reverseOrder();
    Comparator<SZHouseDealStatisticIntegrationDto> dateComparator =
        Comparator.comparing(SZHouseDealStatisticIntegrationDto::getDate).reversed();

    return switch (dateFormat) {
      case WEEK ->
          integratedData.stream()
              .collect(Collectors.groupingBy(dto -> getStartOfWeek(dto.getDate(), startDate)))
              .entrySet()
              .stream()
              .sorted(
                  Map.Entry.<LocalDate, List<SZHouseDealStatisticIntegrationDto>>comparingByKey(
                      localDateComparator))
              .map(entry -> aggregateData(entry.getValue(), entry.getKey()))
              .collect(Collectors.toList());
      case MONTH ->
          integratedData.stream()
              .collect(Collectors.groupingBy(dto -> dto.getDate().withDayOfMonth(1)))
              .entrySet()
              .stream()
              .sorted(
                  Map.Entry.<LocalDate, List<SZHouseDealStatisticIntegrationDto>>comparingByKey(
                      localDateComparator))
              .map(entry -> aggregateData(entry.getValue(), entry.getKey()))
              .collect(Collectors.toList());
      case YEAR ->
          integratedData.stream()
              .collect(Collectors.groupingBy(dto -> dto.getDate().withDayOfYear(1)))
              .entrySet()
              .stream()
              .sorted(
                  Map.Entry.<LocalDate, List<SZHouseDealStatisticIntegrationDto>>comparingByKey(
                      localDateComparator))
              .map(entry -> aggregateData(entry.getValue(), entry.getKey()))
              .collect(Collectors.toList());
      default -> integratedData.stream().sorted(dateComparator).collect(Collectors.toList());
    };
  }

  private LocalDate getStartOfWeek(LocalDate date, LocalDate startDate) {
    long daysBetween = java.time.temporal.ChronoUnit.DAYS.between(startDate, date);
    long weeksBetween = daysBetween / 7;
    return startDate.plusWeeks(weeksBetween);
  }

  /**
   * 聚合一组数据到一个 DTO 对象。
   *
   * @param dtoList 需要聚合的 DTO 列表
   * @param aggregateDate 聚合后的日期（按月或按年）
   * @return 聚合后的 DTO 对象
   */
  private SZHouseDealStatisticIntegrationDto aggregateData(
      List<SZHouseDealStatisticIntegrationDto> dtoList, LocalDate aggregateDate) {

    SZHouseDealStatisticIntegrationDto aggregated = new SZHouseDealStatisticIntegrationDto();
    aggregated.setDate(aggregateDate); // 设置为聚合后的日期（月份或年份的第一天）

    // 使用辅助方法进行汇总
    Double totalNewHouseDealArea =
        sumDoubles(dtoList, SZHouseDealStatisticIntegrationDto::getNewHouseDealArea);

    Integer totalNewHouseDealSetCount =
        sumIntegers(dtoList, SZHouseDealStatisticIntegrationDto::getNewHouseDealSetCount);

    Double totalUsedHouseDealArea =
        sumDoubles(dtoList, SZHouseDealStatisticIntegrationDto::getUsedHouseDealArea);

    Integer totalUsedHouseDealCount =
        sumIntegers(dtoList, SZHouseDealStatisticIntegrationDto::getUsedHouseDealCount);

    Double totalOnlineSubscriptionArea =
        sumDoubles(dtoList, SZHouseDealStatisticIntegrationDto::getOnlineSubscriptionArea);

    Integer totalOnlineSubscriptionCount =
        sumIntegers(dtoList, SZHouseDealStatisticIntegrationDto::getOnlineSubscriptionCount);

    // 设置聚合后的值
    aggregated.setNewHouseDealArea(totalNewHouseDealArea);
    aggregated.setNewHouseDealSetCount(totalNewHouseDealSetCount);
    aggregated.setUsedHouseDealArea(totalUsedHouseDealArea);
    aggregated.setUsedHouseDealCount(totalUsedHouseDealCount);
    aggregated.setOnlineSubscriptionArea(totalOnlineSubscriptionArea);
    aggregated.setOnlineSubscriptionCount(totalOnlineSubscriptionCount);

    return aggregated;
  }

  /**
   * 辅助方法：汇总 Integer 类型字段
   *
   * @param list DTO 列表
   * @param getter 获取 Integer 值的函数
   * @return 汇总的 Integer 值，若所有值均为 null，则返回 null
   */
  private Integer sumIntegers(
      List<SZHouseDealStatisticIntegrationDto> list,
      Function<SZHouseDealStatisticIntegrationDto, Integer> getter) {
    List<Integer> nonNullValues = list.stream().map(getter).filter(Objects::nonNull).toList();

    if (nonNullValues.isEmpty()) {
      return null;
    }

    return nonNullValues.stream().mapToInt(Integer::intValue).sum();
  }

  /**
   * 辅助方法：汇总 Double 类型字段
   *
   * @param list DTO 列表
   * @param getter 获取 Double 值的函数
   * @return 汇总的 Double 值，若所有值均为 null，则返回 null
   */
  private Double sumDoubles(
      List<SZHouseDealStatisticIntegrationDto> list,
      Function<SZHouseDealStatisticIntegrationDto, Double> getter) {
    List<Double> nonNullValues = list.stream().map(getter).filter(Objects::nonNull).toList();

    if (nonNullValues.isEmpty()) {
      return null;
    }

    return nonNullValues.stream().mapToDouble(Double::doubleValue).sum();
  }
}
