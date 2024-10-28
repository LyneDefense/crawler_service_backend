package com.app.tuantuan.model.dto.housedeal;

import com.app.tuantuan.model.entity.housedeal.SZHouseDealsDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商品房成交信息")
public class SZHouseDealsDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("用途")
  private String category;

  @ApiModelProperty("成交套数")
  private int dealCount;

  @ApiModelProperty(" 成交面积(㎡)")
  private double dealArea;

  @ApiModelProperty("月末可售套数")
  private int endMonthSaleCount;

  @ApiModelProperty("月末可售面积(㎡)")
  private double endMonthSaleArea;

  public SZHouseDealsDetailDto(
      String category,
      int dealCount,
      double dealArea,
      int endMonthSaleCount,
      double endMonthSaleArea) {
    this.category = category;
    this.dealCount = dealCount;
    this.dealArea = dealArea;
    this.endMonthSaleCount = endMonthSaleCount;
    this.endMonthSaleArea = endMonthSaleArea;
  }

  public SZHouseDealsDetailDO to(String parentId) {
    return SZHouseDealsDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .category(this.category)
        .dealCount(this.dealCount)
        .dealArea(this.dealArea)
        .endMonthSaleCount(this.endMonthSaleCount)
        .endMonthSaleArea(this.endMonthSaleArea)
        .build();
  }

  public static SZHouseDealsDetailDto of(SZHouseDealsDetailDO houseDealsDetailDO) {
    return SZHouseDealsDetailDto.builder()
        .id(houseDealsDetailDO.getId())
        .category(houseDealsDetailDO.getCategory())
        .dealCount(houseDealsDetailDO.getDealCount())
        .dealArea(houseDealsDetailDO.getDealArea())
        .endMonthSaleCount(houseDealsDetailDO.getEndMonthSaleCount())
        .endMonthSaleArea(houseDealsDetailDO.getEndMonthSaleArea())
        .parentId(houseDealsDetailDO.getParentId())
        .build();
  }

  @Override
  public String toString() {
    return "商品房成交信息{"
        + "用途='"
        + category
        + '\''
        + ", 成交套数="
        + dealCount
        + ", 成交面积(㎡)="
        + dealArea
        + ", 月末可售套数="
        + endMonthSaleCount
        + ", 月末可售面积(㎡)="
        + endMonthSaleArea
        + '}';
  }
}
