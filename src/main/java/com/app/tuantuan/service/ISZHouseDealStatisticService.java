package com.app.tuantuan.service;

import com.app.tuantuan.enumeration.DateFormat;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticDataDto;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticIntegrationDto;
import java.time.LocalDate;
import java.util.List;

public interface ISZHouseDealStatisticService {

  /** 爬取并保存统计数据。只增量爬取 */
  void crawlAndSaveStatisticData();

  /**
   * 根据日期范围获取房屋成交统计数据。
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 包含房屋成交统计数据的 SZHouseDealStatisticDataDto 列表。
   */
  List<SZHouseDealStatisticDataDto> findHouseDealStatisticByDate(
      LocalDate startDate, LocalDate endDate);

  /**
   * 根据日期范围获取房屋成交整合统计数据。
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @param dateFormat 日期格式
   * @return 包含房屋成交整合统计数据的 SZHouseDealStatisticIntegrationDto 列表。
   */
  List<SZHouseDealStatisticIntegrationDto> findHouseDealStatisticIntegrationByDate(
      LocalDate startDate, LocalDate endDate, DateFormat dateFormat);
}
