package com.app.tuantuan.service;

import com.app.tuantuan.model.dto.housedeal.SZHouseDealsInfoDto;
import java.util.List;

public interface ISZHouseDealInfoService {

    /**
     * 获取当天城市的新房成交信息。
     *
     * @param year  年份
     * @param month 月份
     * @return 包含新房成交信息的 SZHouseDealsInfoDto 列表。
     */
    List<SZHouseDealsInfoDto> getTodayHouseDealsInfo(int year, int month);

    /**
     * 爬取并保存当天的房屋成交信息。
     */
    void crawlAndSaveTodayHouseDealsInfo();
}
