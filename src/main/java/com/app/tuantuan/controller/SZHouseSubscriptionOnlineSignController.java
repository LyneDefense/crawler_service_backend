package com.app.tuantuan.controller;

import com.app.tuantuan.model.base.Resp;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignInfoDto;
import com.app.tuantuan.model.dto.onlinesign.presale.SZSubscriptionOnlineSignInfoDto;
import com.app.tuantuan.service.ISZHouseSubscriptionOnlineSingService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/subscription/online/sz")
@Api(value = "SZHouseSubscriptionOnlineSignController", tags = "深圳商品房(预售|现售)网签认购信息控制器")
public class SZHouseSubscriptionOnlineSignController {

  @Resource private ISZHouseSubscriptionOnlineSingService szHouseSubscriptionOnlineSingService;

  @GetMapping("/presale/{date}")
  public Resp<List<SZSubscriptionOnlineSignInfoDto>> selectSubscriptionOnlineSignInfoByDate(
      @PathVariable(value = "date")
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @ApiParam(required = true, value = "查询日期,yyyy-MM-dd")
          LocalDate date) {
    return Resp.data(
        szHouseSubscriptionOnlineSingService.selectSubscriptionOnlineSignInfoByDate(date));
  }

  @PutMapping("/presale/crawl")
  public Resp<Void> crawlAndSaveTodayOnlineSignInfo() {
    szHouseSubscriptionOnlineSingService.crawAndSaveSubscriptionOnlineSignInfo();
    return Resp.ok();
  }

  @GetMapping("/onsale/{date}")
  public Resp<List<SZOnsaleContractOnlineSignInfoDto>> selectOnsaleSubscriptionOnlineSignInfoByDate(
      @PathVariable(value = "date")
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @ApiParam(required = true, value = "查询日期,yyyy-MM-dd")
          LocalDate date) {
    return Resp.data(
        szHouseSubscriptionOnlineSingService.selectOnSaleSubscriptionOnlineSignInfoByDate(date));
  }

  @PutMapping("/onsale/crawl")
  public Resp<Void> crawlAndSaveTodayOnsaleOnlineSignInfo() {
    szHouseSubscriptionOnlineSingService.crawAndSaveSubscriptionOnSaleOnlineSignInfo();
    return Resp.ok();
  }
}
