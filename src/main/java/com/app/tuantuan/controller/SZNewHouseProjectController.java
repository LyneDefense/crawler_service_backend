package com.app.tuantuan.controller;

import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.base.Resp;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageReqDto;
import com.app.tuantuan.service.ISZNewHouseProjectService;
import io.swagger.annotations.Api;
import javax.annotation.Resource;
import javax.validation.Valid;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/new_house")
@Api(value = "SZNewHouseProjectController", tags = "深圳一手房源公示相关信息控制器")
public class SZNewHouseProjectController {

  @Resource ISZNewHouseProjectService szNewHouseProjectService;

  @PostMapping("/main_page/page")
  public Resp<PageResult<NewHouseMainPageItemDto>> selectNewHouseMainPageItem(
      @RequestBody @Valid NewHouseMainPageReqDto reqDto) {
    return Resp.data(szNewHouseProjectService.selectNewHouseMainPageItem(reqDto));
  }

  @PutMapping("/main_page/crawl")
  public Resp<Void> crawlAndSaveMainPageItems(@RequestBody @Valid NewHouseMainPageReqDto reqDto) {
    szNewHouseProjectService.crawlAndSaveMainPageItems(reqDto);
    return Resp.ok();
  }
}
