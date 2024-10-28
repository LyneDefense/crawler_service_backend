package com.app.tuantuan.service;

import com.app.tuantuan.model.dto.onlinesign.SZSubscriptionOnlineSignInfoDto;
import java.time.LocalDate;
import java.util.List;

public interface ISZHouseSubscriptionOnlineSingService {

  /**
   * 根据日期查询认购网签信息。
   *
   * @param date 日期
   * @return 包含认购网签信息的 SZSubscriptionOnlineSignInfoDto 列表。
   */
  List<SZSubscriptionOnlineSignInfoDto> selectSubscriptionOnlineSignInfoByDate(LocalDate date);

  /** 爬取并保存认购网签信息。 */
  void crawAndSaveSubscriptionOnlineSignInfo();
}
