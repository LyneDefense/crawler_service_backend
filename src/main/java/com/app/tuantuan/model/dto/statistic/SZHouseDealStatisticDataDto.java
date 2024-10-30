package com.app.tuantuan.model.dto.statistic;

import com.app.tuantuan.model.entity.statistic.SZHouseDealStatisticDataDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("深圳商品房成交统计信息")
public class SZHouseDealStatisticDataDto {

  private LocalDate date;

  @ApiModelProperty("一手房成交面积")
  private double newHouseDealArea;

  @ApiModelProperty("一手房成交量(套)")
  private int newHouseDealSetCount;

  @ApiModelProperty("二手房成交面积")
  private double usedHouseDealArea;

  @ApiModelProperty("二手房成交量(套)")
  private int usedHouseDealCount;

  public static SZHouseDealStatisticDataDto of(SZHouseDealStatisticDataDO doObject) {
    if (doObject == null) {
      return null;
    }

    return SZHouseDealStatisticDataDto.builder()
        .date(doObject.getDate())
        .newHouseDealArea(doObject.getNewHouseDealArea())
        .newHouseDealSetCount(doObject.getNewHouseDealSetCount())
        .usedHouseDealArea(doObject.getUsedHouseDealArea())
        .usedHouseDealCount(doObject.getUsedHouseDealCount())
        .build();
  }

  public SZHouseDealStatisticDataDO to(String id) {
    return SZHouseDealStatisticDataDO.builder()
        .id(id)
        .date(this.getDate())
        .newHouseDealArea(this.getNewHouseDealArea())
        .newHouseDealSetCount(this.getNewHouseDealSetCount())
        .usedHouseDealArea(this.getUsedHouseDealArea())
        .usedHouseDealCount(this.getUsedHouseDealCount())
        .build();
  }
}
