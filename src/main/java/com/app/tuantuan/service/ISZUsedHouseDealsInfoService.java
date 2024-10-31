package com.app.tuantuan.service;

import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsInfoDto;
import java.time.LocalDate;
import java.util.List;

public interface ISZUsedHouseDealsInfoService {

  /** 爬取并保存二手房交易信息。 */
  void crawlAndSaveUseHouseDealsInfo();

  /**
   * 根据日期查询二手房交易信息。
   *
   * @param date 日期
   * @return 二手房交易信息
   */
  List<SZUsedHouseDealsInfoDto> selectUsedHouseDealsInfoByDate(LocalDate date);
}
