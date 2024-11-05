package com.app.tuantuan.service.impl;

import cn.hutool.core.util.StrUtil;
import com.app.tuantuan.crawler.newhouse.NewHouseBuildingInfoCrawler;
import com.app.tuantuan.crawler.newhouse.NewHouseMainPageCrawler;
import com.app.tuantuan.crawler.newhouse.NewHouseSalesInfoCrawler;
import com.app.tuantuan.enumeration.CrawlStatus;
import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.dto.newhouse.*;
import com.app.tuantuan.model.entity.newhouse.NewHouseMainPageItemDO;
import com.app.tuantuan.repository.SZNewHouseMainPageRepository;
import com.app.tuantuan.repository.SZNewHouseProjectRepository;
import com.app.tuantuan.service.ISZNewHouseProjectService;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StopWatch;

@Slf4j
@Service
public class SZNewHouseProjectServiceImpl implements ISZNewHouseProjectService {

  @Resource NewHouseMainPageCrawler newHouseMainPageCrawler;
  @Resource NewHouseSalesInfoCrawler newHouseSalesInfoCrawler;
  @Resource NewHouseBuildingInfoCrawler newHouseBuildingInfoCrawler;
  @Resource SZNewHouseProjectRepository szNewHouseProjectRepository;
  @Resource SZNewHouseMainPageRepository newHouseMainPageRepository;

  @Override
  public PageResult<NewHouseMainPageItemDto> selectNewHouseMainPageItem(
      NewHouseMainPageReqDto reqDto) {
    LocalDate maxApprovalDate = newHouseMainPageRepository.selectMaxApprovalDate();
    List<NewHouseMainPageItemDto> dtos = new ArrayList<>();
    LocalDate extraStartDate =
        maxApprovalDate == null ? LocalDate.of(2020, 1, 1) : maxApprovalDate.plusDays(1);
    dtos =
        newHouseMainPageCrawler.crawl(new NewHouseMainPageReqDto(extraStartDate, LocalDate.now()));

    if (!dtos.isEmpty()) {
      log.info("[发现有更新的一手房源公示首页信息,数量:{}，先保存，再获取最新结果]", dtos.size());
      newHouseMainPageRepository.saveMainPageItems(dtos);
    }
    PageResult<NewHouseMainPageItemDO> entities =
        newHouseMainPageRepository.selectNewHouseMainPageItem(reqDto);
    return new PageResult<>(
        entities.getList().stream().map(NewHouseMainPageItemDto::of).toList(), entities.getTotal());
  }

  @Transactional
  public CrawlStatus crawlAndSaveProject(NewHouseMainPageItemDto maiPageItem) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    try {
      NewHouseSalesMixedInfoDto mixedSalesInfo = newHouseSalesInfoCrawler.crawl(maiPageItem);
      // 检查是否为住宅项目
      if (!mixedSalesInfo.getSalesInfo().getHouseUsage().contains("住宅")) {
        log.info(
            "[楼盘名称:{}, 预售证号:{} 不是住宅，跳过爬取]",
            maiPageItem.getProjectName(),
            maiPageItem.getPreSaleNumber());
        return CrawlStatus.SKIPPED_NOT_RESIDENTIAL;
      }

      List<NewHouseBuildingInfoDto> dtos = new ArrayList<>();
      for (NewHouseSalesBuildingInfoDto salesBuildingInfoDto : mixedSalesInfo.getBuildingInfos()) {
        NewHouseBuildingInfoDto buildingInfoDto =
            newHouseBuildingInfoCrawler.crawl(salesBuildingInfoDto);
        dtos.add(buildingInfoDto);
      }

      szNewHouseProjectRepository.saveAllNewHouseInfoByMainPageItem(
          maiPageItem, mixedSalesInfo, dtos);

      stopWatch.stop();
      log.info(
          "[保存一手房源公示首页信息成功, 楼盘名称:{}, 预售证号:{}, 消耗时间:{}]",
          maiPageItem.getProjectName(),
          maiPageItem.getPreSaleNumber(),
          this.calculateTimeCost(stopWatch.getTotalTimeMillis()));

      return CrawlStatus.SUCCESS;

    } catch (Exception e) {
      stopWatch.stop();
      log.error(
          "[保存一手房源公示首页信息失败, 楼盘名称:{}, 预售证号:{}, 消耗时间:{}]",
          maiPageItem.getProjectName(),
          maiPageItem.getPreSaleNumber(),
          this.calculateTimeCost(stopWatch.getTotalTimeMillis()),
          e);
      return CrawlStatus.FAILURE;
    }
  }

  @Override
  @Transactional
  public void crawlAndSaveProject(List<NewHouseMainPageItemDto> maiPageItems) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    List<CrawlStatus> statusList =
        maiPageItems.parallelStream().map(this::crawlAndSaveProject).toList();
    stopWatch.stop();
    long total = statusList.size();
    long skipped =
        statusList.stream().filter(status -> status == CrawlStatus.SKIPPED_NOT_RESIDENTIAL).count();
    long success = statusList.stream().filter(status -> status == CrawlStatus.SUCCESS).count();
    long failure = statusList.stream().filter(status -> status == CrawlStatus.FAILURE).count();
    log.info(
        "[批量爬取结果汇总] 总共需要处理楼盘数: {}, 跳过数: {}, 成功数: {}, 失败数: {}，总耗时: {}",
        total,
        skipped,
        success,
        failure,
        this.calculateTimeCost(stopWatch.getTotalTimeMillis()));
  }

  @Override
  @Transactional
  public void crawlAndSaveMainPageItems(LocalDate startDate, LocalDate endDate) {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();

    // 爬取首页项目信息
    List<NewHouseMainPageItemDto> dtos =
        newHouseMainPageCrawler.crawl(new NewHouseMainPageReqDto(startDate, endDate));
    log.info("[解析到 {}->{} 一手房源公示首页信息数量:{}]", startDate, endDate, dtos.size());

    Map<CrawlStatus, Long> statusCountMap =
        dtos.parallelStream()
            .map(this::crawlAndSaveProject)
            .collect(Collectors.groupingByConcurrent(status -> status, Collectors.counting()));

    long total = dtos.size();
    long skipped = statusCountMap.getOrDefault(CrawlStatus.SKIPPED_NOT_RESIDENTIAL, 0L);
    long success = statusCountMap.getOrDefault(CrawlStatus.SUCCESS, 0L);
    long failure = statusCountMap.getOrDefault(CrawlStatus.FAILURE, 0L);

    stopWatch.stop();

    log.info(
        "[保存 {} -> {} 一手房源公示首页信息完成, 总处理数:{}, 跳过数:{}, 成功数:{}, 失败数:{}, 总消耗时间:{}]",
        startDate,
        endDate,
        total,
        skipped,
        success,
        failure,
        this.calculateTimeCost(stopWatch.getTotalTimeMillis()));
  }

  private String calculateTimeCost(long totalTimeMillis) {
    long hours = totalTimeMillis / 3600000;
    long minutes = (totalTimeMillis % 3600000) / 60000;
    long seconds = (totalTimeMillis % 60000) / 1000;
    long millis = totalTimeMillis % 1000;
    return StrUtil.format("{}小时，{}分钟,{}秒,{}毫秒", hours, minutes, seconds, millis);
  }
}
