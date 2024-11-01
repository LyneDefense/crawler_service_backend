package com.app.tuantuan.service;

import com.app.tuantuan.model.base.PageParam;
import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageReqDto;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;

public interface ISZNewHouseProjectService {

  /**
   * 根据请求参数选择新房主页面项。
   *
   * @param reqDto 请求参数对象
   * @return 包含新房主页面项的 NewHouseMainPageItemDto 列表。
   */
  PageResult<NewHouseMainPageItemDto> selectNewHouseMainPageItem(
      NewHouseMainPageReqDto reqDto);

  /**
   * 爬取并保存主页面项。
   *
   * @param reqDto 请求参数对象
   */
  void crawlAndSaveMainPageItems(NewHouseMainPageReqDto reqDto);

  /**
   * 删除主页面项。
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   */
  void deleteMaiPageItems(@NotNull LocalDate startDate, @NotNull LocalDate endDate);
}
