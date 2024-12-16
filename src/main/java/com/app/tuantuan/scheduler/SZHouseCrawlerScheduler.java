package com.app.tuantuan.scheduler;

import com.app.tuantuan.model.dto.newhouse.SZNewHouseProjectDto;
import com.app.tuantuan.service.*;
import com.app.tuantuan.service.caller.CrawlerUpdateServiceCaller;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SZHouseCrawlerScheduler {

  @Resource ISZHouseDealInfoService szHouseDealInfoService;
  @Resource ISZHouseSubscriptionOnlineSingService subscriptionOnlineSingService;
  @Resource ISZHouseDealStatisticService statisticService;
  @Resource ISZUsedHouseDealsInfoService usedHouseDealsInfoService;
  @Resource ISZNewHouseProjectService szNewHouseProjectService;
  @Resource CrawlerUpdateServiceCaller crawlerUpdateServiceCaller;

  /** 区域房产交易数据定时任务：每天凌晨12点执行 */
  @Scheduled(cron = "0 0 0 * * ?")
  public void crawlSZHouseDealData() {
    szHouseDealInfoService.crawlAndSaveTodayHouseDealsInfo();
    log.info("[执行区域房产交易数据定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 区域房产交易数据定时任务：每天凌晨12点和中午12点执行 */
  @Scheduled(cron = "0 0 0,12 * * ?")
  public void crawlSZHouseOnlineSign() {
    subscriptionOnlineSingService.crawAndSaveSubscriptionOnlineSignInfo();
    log.info("[执行爬取深圳“预售”网签认购信息定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 房地产成交趋势爬取定时任务：每天凌晨12点和中午12点执行 */
  @Scheduled(cron = "0 0 0,12 * * ?")
  public void crawSZHouseDealStatisticData() {
    statisticService.crawlAndSaveStatisticData();
    log.info("[执行爬取房地产成交趋势爬取定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 区域房产交易数据定时任务：每天凌晨12点和中午12点执行 */
  @Scheduled(cron = "0 0 0,12 * * ?")
  public void crawlSZHouseOnSaleOnlineSign() {
    subscriptionOnlineSingService.crawAndSaveSubscriptionOnSaleOnlineSignInfo();
    log.info("[执行爬取深圳“现售”网签认购信息定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 区域二手房产交易数据定时任务：每天凌晨12点和中午12点执行 */
  @Scheduled(cron = "0 0 0,12 * * ?")
  public void crawlSZUsedHouseDealsInfo() {
    usedHouseDealsInfoService.crawlAndSaveUseHouseDealsInfo();
    log.info("[执行爬取深圳二手房交易信息定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 一手房源公示首页信息爬取定时任务：每天凌晨1点，除了周天 */
  @Scheduled(cron = "0 0 1 * * 1-6")
  public void crawNewHouseMainPageItemsAndBuildingsDaily() {
    LocalDate startDate = LocalDate.now().minusMonths(6);
    log.info(
        "[执行每日一手房源公示首页信息爬取定时任务,爬取日期:{}-{},时间:{}]", startDate, LocalDate.now(), LocalDateTime.now());
    List<SZNewHouseProjectDto> dtos =
        szNewHouseProjectService.crawlAndSaveMainPageItems(startDate, LocalDate.now());
    dtos.forEach(e -> crawlerUpdateServiceCaller.updateCrawlerData(e));
    log.info("[执行一手每日房源公示首页信息爬取定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 一手房源公示首页信息爬取定时任务：每周天凌晨1点 */
  @Scheduled(cron = "0 0 1 * * 0")
  public void crawNewHouseMainPageItemsAndBuildingsDailyWeekly() {
    LocalDate startDate = LocalDate.now().minusYears(1);
    log.info(
        "[执行每周一手房源公示首页信息爬取定时任务成功,爬取日期:{}-{},时间:{}]",
        startDate,
        LocalDate.now(),
        LocalDateTime.now());
    List<SZNewHouseProjectDto> dtos =
        szNewHouseProjectService.crawlAndSaveMainPageItems(startDate, LocalDate.now());
    dtos.forEach(e -> crawlerUpdateServiceCaller.updateCrawlerData(e));
    log.info("[执行每周一手房源公示首页信息爬取定时任务成功,时间:{}]", LocalDateTime.now());
  }
}
