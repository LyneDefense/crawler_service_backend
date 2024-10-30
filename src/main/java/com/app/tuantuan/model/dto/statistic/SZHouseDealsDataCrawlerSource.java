package com.app.tuantuan.model.dto.statistic;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

/** 房地产成交趋势接口返回的原始数据 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SZHouseDealsDataCrawlerSource {

  @JsonProperty("date")
  private List<String> date;

  @JsonProperty("result")
  private String result;

  @JsonProperty("ysfDealArea")
  private List<Double> ysfDealArea;

  @JsonProperty("ysfTotalTs")
  private List<Integer> ysfTotalTs;

  @JsonProperty("esfDealArea")
  private List<Double> esfDealArea;

  @JsonProperty("bigEventCont")
  private List<Object> bigEventCont;

  @JsonProperty("esfTotalTs")
  private List<Integer> esfTotalTs;
}
