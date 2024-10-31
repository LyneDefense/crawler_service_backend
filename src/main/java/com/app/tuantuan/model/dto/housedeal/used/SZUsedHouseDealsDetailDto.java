package com.app.tuantuan.model.dto.housedeal.used;

import com.app.tuantuan.model.entity.housedeal.used.SZUsedHouseDealsDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("深圳二手商品住房成交信息详情")
public class SZUsedHouseDealsDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("用途")
  private String category;

  @ApiModelProperty("成交套数")
  private Integer salesCount;

  @ApiModelProperty("成交面积")
  private Double salesArea;

  public SZUsedHouseDealsDetailDO to(String parentId) {
    return SZUsedHouseDealsDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .category(this.category)
        .salesCount(this.salesCount)
        .salesArea(this.salesArea)
        .build();
  }

  public static SZUsedHouseDealsDetailDto of(SZUsedHouseDealsDetailDO entity) {
    return SZUsedHouseDealsDetailDto.builder()
        .id(entity.getId())
        .parentId(entity.getParentId())
        .category(entity.getCategory())
        .salesCount(entity.getSalesCount())
        .salesArea(entity.getSalesArea())
        .build();
  }
}
