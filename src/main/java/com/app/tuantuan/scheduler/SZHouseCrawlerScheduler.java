package com.app.tuantuan.scheduler;

import com.app.tuantuan.service.ISZHouseDealInfoService;
import com.app.tuantuan.service.ISZHouseDealStatisticService;
import com.app.tuantuan.service.ISZHouseSubscriptionOnlineSingService;
import java.time.LocalDate;
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

  /** 区域房产交易数据定时任务：每天凌晨12点执行 */
  @Scheduled(cron = "0 0 0 * * ?")
  public void crawlSZHouseDealData() {
    szHouseDealInfoService.crawlAndSaveTodayHouseDealsInfo();
    log.info("[执行区域房产交易数据定时任务成功,时间:{}]", LocalDate.now());
  }

  /** 区域房产交易数据定时任务：每天凌晨12点和中午12点执行 */
  @Scheduled(cron = "0 0 0,12 * * ?")
  public void crawlSZHouseOnlineSignCrawler() {
    subscriptionOnlineSingService.crawAndSaveSubscriptionOnlineSignInfo();
    log.info("[执行爬取深圳网签认购信息定时任务成功,时间:{}]", LocalDate.now());
  }

  /** 房地产成交趋势爬取定时任务：每天凌晨12点和中午12点执行 */
  @Scheduled(cron = "0 0 0,12 * * ?")
  public void crawSZHouseDealStatisticData() {
    statisticService.crawlAndSaveStatisticData();
    log.info("[执行爬取房地产成交趋势爬取定时任务成功,时间:{}]", LocalDate.now());
  }
}
