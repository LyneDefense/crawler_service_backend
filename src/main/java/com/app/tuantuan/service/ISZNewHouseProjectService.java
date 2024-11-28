package com.app.tuantuan.service;

import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageReqDto;
import com.app.tuantuan.model.dto.newhouse.SZNewHouseProjectDto;
import java.time.LocalDate;
import java.util.List;

public interface ISZNewHouseProjectService {

  /**
   * 根据请求参数选择新房主页面项。
   *
   * @param reqDto 请求参数对象
   * @return 包含新房主页面项的 NewHouseMainPageItemDto 列表。
   */
  PageResult<NewHouseMainPageItemDto> selectNewHouseMainPageItem(NewHouseMainPageReqDto reqDto);

  List<SZNewHouseProjectDto> crawlAndSaveProject(List<NewHouseMainPageItemDto> maiPageItems);

  List<SZNewHouseProjectDto> crawlAndSaveMainPageItems(LocalDate startDate, LocalDate endDate);

  void crawlerTodayBeforeOneYearItems();

  void syncCurrentItemsToBackendService(LocalDate startDate, LocalDate endDate);
}
