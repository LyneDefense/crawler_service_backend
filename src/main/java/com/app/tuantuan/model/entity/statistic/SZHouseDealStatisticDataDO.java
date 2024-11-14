package com.app.tuantuan.model.entity.statistic;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_house_deal_statistic_data", autoResultMap = true)
public class SZHouseDealStatisticDataDO extends BaseDO {

  private String id;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate date;

  /** 一手房成交面积 */
  private double newHouseDealArea;

  /** 一手房成交量(套) */
  private int newHouseDealSetCount;

  /** 二手房成交面积 */
  private double usedHouseDealArea;

  /** 二手房成交量(套) */
  private int usedHouseDealCount;
}
