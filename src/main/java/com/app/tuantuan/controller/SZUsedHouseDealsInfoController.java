package com.app.tuantuan.controller;

import com.app.tuantuan.model.base.Resp;
import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsInfoDto;
import com.app.tuantuan.service.ISZUseHouseDealsInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/house_deal/used/sz")
@Api(value = "SZHouseDealInfoController", tags = "深圳二手商品房成交信息控制器")
public class SZUsedHouseDealsInfoController {

  @Resource ISZUseHouseDealsInfoService useHouseDealsInfoService;

  @GetMapping("/{date}")
  public Resp<List<SZUsedHouseDealsInfoDto>> selectSubscriptionOnlineSignInfoByDate(
      @PathVariable(value = "date")
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @ApiParam(required = true, value = "查询日期,yyyy-MM-dd")
          LocalDate date) {
    return Resp.data(useHouseDealsInfoService.selectUsedHouseDealsInfoByDate(date));
  }

  @PutMapping("/crawl")
  public Resp<Void> crawlAndSaveTodayOnlineSignInfo() {
    useHouseDealsInfoService.crawlAndSaveUseHouseDealsInfo();
    return Resp.ok();
  }
}
