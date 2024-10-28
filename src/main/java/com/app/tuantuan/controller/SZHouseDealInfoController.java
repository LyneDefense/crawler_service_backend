package com.app.tuantuan.controller;

import com.app.tuantuan.model.dto.housedeal.SZHouseDealsInfoDto;
import com.app.tuantuan.service.ISZHouseDealInfoService;
import io.swagger.annotations.Api;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/house_deal/sz")
@Api(value = "SZHouseDealInfoController", tags = "商品房成交信息控制器")
public class SZHouseDealInfoController {

  @Resource ISZHouseDealInfoService szHouseDealInfoService;

  @GetMapping("/today/{year}/{month}")
  public List<SZHouseDealsInfoDto> getTodayHouseDealsInfo(
      @PathVariable(value = "year") int year, @PathVariable(value = "month") int month) {
    return szHouseDealInfoService.getTodayHouseDealsInfo(year, month);
  }

  @PutMapping("/today/crawl")
  public void crawlAndSaveTodayHouseDealsInfo() {
    szHouseDealInfoService.crawlAndSaveTodayHouseDealsInfo();
  }
}