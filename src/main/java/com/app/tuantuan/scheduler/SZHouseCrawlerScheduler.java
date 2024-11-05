package com.app.tuantuan.scheduler;

import com.app.tuantuan.service.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StopWatch;

@Slf4j
@Component
public class SZHouseCrawlerScheduler {

  @Resource ISZHouseDealInfoService szHouseDealInfoService;
  @Resource ISZHouseSubscriptionOnlineSingService subscriptionOnlineSingService;
  @Resource ISZHouseDealStatisticService statisticService;
  @Resource ISZUsedHouseDealsInfoService usedHouseDealsInfoService;
  @Resource ISZNewHouseProjectService szNewHouseProjectService;

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

  /** 一手房源公示首页信息爬取定时任务：每天凌晨1点，除了周三 */
  @@Scheduled(cron = "0 0 1 * * 1-3,5-7")
  public void crawNewHouseMainPageItemsAndBuildingsDaily() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    LocalDate startDate = LocalDate.now().minusYears(1);
    log.info(
        "[执行每日一手房源公示首页信息爬取定时任务,爬取日期:{}-{},时间:{}]", startDate, LocalDate.now(), LocalDateTime.now());
    szNewHouseProjectService.crawlAndSaveMainPageItems(startDate, LocalDate.now());
    stopWatch.stop();
    log.info("[执行一手每日房源公示首页信息爬取定时任务成功,时间:{}]", LocalDateTime.now());
  }

  /** 一手房源公示首页信息爬取定时任务：每周三凌晨2点 */
  @Scheduled(cron = "0 0 1 * * 4")
  public void crawNewHouseMainPageItemsAndBuildingsDailyWeekly() {
    StopWatch stopWatch = new StopWatch();
    stopWatch.start();
    LocalDate startDate = LocalDate.of(2020, 1, 1);
    log.info(
        "[执行每周一手房源公示首页信息爬取定时任务成功,爬取日期:{}-{},时间:{}]",
        startDate,
        LocalDate.now(),
        LocalDateTime.now());
    szNewHouseProjectService.crawlAndSaveMainPageItems(startDate, LocalDate.now());
    log.info("[执行每周一手房源公示首页信息爬取定时任务成功,时间:{}]", LocalDateTime.now());
  }
}
