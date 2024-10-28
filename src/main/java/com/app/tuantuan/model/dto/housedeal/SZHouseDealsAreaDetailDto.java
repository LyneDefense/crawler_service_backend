package com.app.tuantuan.model.dto.housedeal;

import com.app.tuantuan.model.entity.housedeal.SZHouseDealsArealDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商品住房按面积统计成交信息")
public class SZHouseDealsAreaDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("面积区间")
  private String areaRange;

  @ApiModelProperty("成交套数")
  private int dealCount;

  @ApiModelProperty("成交面积(㎡)")
  private double dealArea;

  public SZHouseDealsAreaDetailDto(String areaRange, int dealCount, double dealArea) {
    this.areaRange = areaRange;
    this.dealCount = dealCount;
    this.dealArea = dealArea;
  }

  public SZHouseDealsArealDetailDO to(String parentId) {
    return SZHouseDealsArealDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .areaRange(this.areaRange)
        .dealArea(this.dealArea)
        .dealCount(this.dealCount)
        .build();
  }

  public static SZHouseDealsAreaDetailDto of(SZHouseDealsArealDetailDO SZHouseDealsArealDetailDO) {
    return SZHouseDealsAreaDetailDto.builder()
        .id(SZHouseDealsArealDetailDO.getId())
        .parentId(SZHouseDealsArealDetailDO.getParentId())
        .areaRange(SZHouseDealsArealDetailDO.getAreaRange())
        .dealCount(SZHouseDealsArealDetailDO.getDealCount())
        .dealArea(SZHouseDealsArealDetailDO.getDealArea())
        .build();
  }

  @Override
  public String toString() {
    return "商品住房按面积统计成交信息{"
        + "面积区间='"
        + areaRange
        + '\''
        + ", 成交套数="
        + dealCount
        + ", 成交面积="
        + dealArea
        + '}';
  }
}
