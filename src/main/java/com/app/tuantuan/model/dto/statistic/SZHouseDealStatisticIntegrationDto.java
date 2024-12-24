package com.app.tuantuan.model.dto.statistic;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("深圳商品房成交整合统计信息")
public class SZHouseDealStatisticIntegrationDto {

  private LocalDate date;

  @ApiModelProperty("一手房成交面积")
  private Double newHouseDealArea;

  @ApiModelProperty("一手房成交量(套)")
  private Integer newHouseDealSetCount;

  @ApiModelProperty("二手房成交面积")
  private Double usedHouseDealArea;

  @ApiModelProperty("二手房成交量(套)")
  private Integer usedHouseDealCount;

  @ApiModelProperty("网签认购面积")
  private Double onlineSubscriptionArea;

  @ApiModelProperty("网签认购套数")
  private Integer onlineSubscriptionCount;
}
