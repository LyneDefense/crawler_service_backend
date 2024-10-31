package com.app.tuantuan.model.dto.onlinesign.presale;

import com.app.tuantuan.model.entity.onlinesign.preale.SZContractOnlineSignDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商品房购房合同网签信息")
public class SZContractOnlineSignDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("用途")
  private String category;

  @ApiModelProperty("购房合同网签套数")
  private int contractOnlineSignCount;

  @ApiModelProperty("购房合同网签面积")
  private double contractOnlineSignArea;

  @ApiModelProperty("可售套数")
  private int availableSalesCount;

  @ApiModelProperty("可售面积")
  private double availableSalesArea;

  public SZContractOnlineSignDetailDto(
      String category,
      int contractOnlineSignCount,
      double contractOnlineSignArea,
      int availableSalesCount,
      double availableSalesArea) {
    this.category = category;
    this.contractOnlineSignCount = contractOnlineSignCount;
    this.contractOnlineSignArea = contractOnlineSignArea;
    this.availableSalesCount = availableSalesCount;
    this.availableSalesArea = availableSalesArea;
  }

  public SZContractOnlineSignDetailDO to(String parentId) {
    return SZContractOnlineSignDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .category(this.category)
        .contractOnlineSignCount(this.contractOnlineSignCount)
        .contractOnlineSignArea(this.contractOnlineSignArea)
        .availableSalesCount(this.availableSalesCount)
        .availableSalesArea(this.availableSalesArea)
        .build();
  }

  public static SZContractOnlineSignDetailDto of(SZContractOnlineSignDetailDO entity) {
    return SZContractOnlineSignDetailDto.builder()
        .id(entity.getId())
        .parentId(entity.getParentId())
        .category(entity.getCategory())
        .contractOnlineSignCount(entity.getContractOnlineSignCount())
        .contractOnlineSignArea(entity.getContractOnlineSignArea())
        .availableSalesCount(entity.getAvailableSalesCount())
        .availableSalesArea(entity.getAvailableSalesArea())
        .build();
  }
}
