package com.app.tuantuan.controller;

import com.app.tuantuan.model.base.Resp;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticDataDto;
import com.app.tuantuan.service.ISZHouseDealStatisticService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiParam;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/house_deal/sz/statistic")
@Api(value = "SZHouseDealStatisticController", tags = "深圳商品房成交统计(历史)数据控制器")
public class SZHouseDealStatisticController {

  @Resource ISZHouseDealStatisticService houseDealStatisticService;

  @PutMapping("/crawl")
  public Resp<Void> crawlAndSaveStatisticData() {
    houseDealStatisticService.crawlAndSaveStatisticData();
    return Resp.ok();
  }

  @GetMapping("/get")
  public Resp<List<SZHouseDealStatisticDataDto>> findHouseDealStatisticByDate(
      @RequestParam(name = "start_date")
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @ApiParam(required = true, value = "开始日期,yyyy-MM-dd")
          LocalDate startDate,
      @RequestParam(name = "end_date")
          @DateTimeFormat(pattern = "yyyy-MM-dd")
          @ApiParam(required = true, value = "结束日期,yyyy-MM-dd")
          LocalDate endDate) {
    return Resp.data(houseDealStatisticService.findHouseDealStatisticByDate(startDate, endDate));
  }
}
