package com.app.tuantuan.controller;

import com.app.tuantuan.model.dto.onlinesign.SZSubscriptionOnlineSignInfoDto;
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
@Api(value = "SZHouseSubscriptionOnlineSignController", tags = "深圳商品房网签认购信息控制器")
public class SZHouseSubscriptionOnlineSignController {

  @Resource private ISZHouseSubscriptionOnlineSingService szHouseSubscriptionOnlineSingService;

  @GetMapping("/{date}")
  public List<SZSubscriptionOnlineSignInfoDto> selectSubscriptionOnlineSignInfoByDate(
      @PathVariable(value = "date")
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @ApiParam(required = true, value = "查询日期,yyyy-MM-dd")
          LocalDate date) {
    return szHouseSubscriptionOnlineSingService.selectSubscriptionOnlineSignInfoByDate(date);
  }

  @PutMapping("/crawl")
  public void crawlAndSaveTodayHouseDealsInfo() {
    szHouseSubscriptionOnlineSingService.crawAndSaveSubscriptionOnlineSignInfo();
  }
}
