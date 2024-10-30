package com.app.tuantuan.mapper;

import com.app.tuantuan.mapper.base.BaseMapperX;
import com.app.tuantuan.model.entity.statistic.SZHouseDealStatisticDataDO;
import org.apache.ibatis.annotations.Select;

import java.time.LocalDate;

public interface SZHouseDealStatisticDataMapper extends BaseMapperX<SZHouseDealStatisticDataDO> {

  /**
   * 查询 date 字段的最大日期
   *
   * @return 最大的 LocalDate
   */
  @Select("SELECT MAX(date) FROM public.sz_house_deal_statistic_data")
  LocalDate findMaxDate();
}
