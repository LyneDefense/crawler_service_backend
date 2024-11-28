package com.app.tuantuan.controller;

import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.base.Resp;
import com.app.tuantuan.model.dto.newhouse.*;
import com.app.tuantuan.service.ISZNewHouseProjectService;
import com.app.tuantuan.service.caller.CrawlerUpdateServiceCaller;
import io.swagger.annotations.Api;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/new_house")
@Api(value = "SZNewHouseProjectController", tags = "深圳一手房源公示相关信息控制器")
public class SZNewHouseProjectController {

  @Resource ISZNewHouseProjectService szNewHouseProjectService;
  @Resource CrawlerUpdateServiceCaller crawlerUpdateServiceCaller;

  @PostMapping("/main_page/page")
  public Resp<PageResult<NewHouseMainPageItemDto>> selectNewHouseMainPageItem(
      @RequestBody @Valid NewHouseMainPageReqDto reqDto) {
    return Resp.data(szNewHouseProjectService.selectNewHouseMainPageItem(reqDto));
  }

  @PutMapping("/building/crawl")
  public Resp<Void> crawlAndSaveBuildings(
      @RequestBody @Valid NewHouseBuildingCrawlerReqDto reqDto) {
    List<SZNewHouseProjectDto> dtos =
        szNewHouseProjectService.crawlAndSaveProject(reqDto.getMainPageItems());
    dtos.forEach(d -> crawlerUpdateServiceCaller.updateCrawlerData(d));
    return Resp.ok();
  }

  @PutMapping("/building/test/crawl")
  public Resp<Void> crawlMainPageBuildings() {
    List<SZNewHouseProjectDto> dtos =
        szNewHouseProjectService.crawlAndSaveMainPageItems(
            LocalDate.of(2024, 7, 24), LocalDate.of(2024, 7, 24));
    dtos.forEach(d -> crawlerUpdateServiceCaller.updateCrawlerData(d));
    return Resp.ok();
  }

  @PostMapping("/building/crawl/today")
  public Resp<Void> crawTodayToOneYearBeforeItem() {
    szNewHouseProjectService.crawlerTodayBeforeOneYearItems();
    return Resp.ok();
  }

  @PostMapping("/building/crawl/sync")
  public Resp<Void> syncCurrentItemsToBackendService(@RequestBody @Valid SyncMainPageReqDto dto) {
    szNewHouseProjectService.syncCurrentItemsToBackendService(dto.getStartDate(), dto.getEndDate());
    return Resp.ok();
  }
}
