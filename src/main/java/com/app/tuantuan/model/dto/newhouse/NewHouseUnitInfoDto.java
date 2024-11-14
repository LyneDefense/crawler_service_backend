package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.enumeration.HouseStatus;
import com.app.tuantuan.model.entity.newhouse.NewHouseUnitInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("套房详细信息")
public class NewHouseUnitInfoDto {

  private String id;

  private String blockId;

  @ApiModelProperty("套房状态")
  private HouseStatus status;

  @ApiModelProperty("座号")
  private String blockNumber;

  @ApiModelProperty("楼层")
  private Integer floorNumber;

  @ApiModelProperty("房号")
  private String unitSourceNumber;

  @ApiModelProperty("解析后的房号")
  private String unitNumber;

  @ApiModelProperty("用途")
  private String usage;

  @ApiModelProperty("是否保障性住房")
  private Boolean accessibility;

  @ApiModelProperty("拟售价格-按建筑面积计")
  private Double constructionPrice;

  @ApiModelProperty("拟售价格-按套内面积计")
  private Double grossFloorPrice;

  @ApiModelProperty("预售查丈-建筑面积")
  private Double preSaleConstructionArea;

  @ApiModelProperty("预售查丈-套内面积")
  private Double preSaleGrossFloorArea;

  @ApiModelProperty("预售查丈-分摊面积")
  private Double preSaleSharedArea;

  @ApiModelProperty("竣工查丈-建筑面积")
  private Double preCompletionConstructionArea;

  @ApiModelProperty("竣工查丈-套内面积")
  private Double preCompletionGrossFloorArea;

  @ApiModelProperty("竣工查丈-分摊面积")
  private Double preCompletionSharedArea;

  public NewHouseUnitInfoDO to(String id, String blockId) {
    return NewHouseUnitInfoDO.builder()
        .id(id)
        .blockId(blockId)
        .status(this.status)
        .blockNumber(this.blockNumber)
        .floorNumber(this.floorNumber)
        .unitSourceNumber(this.unitSourceNumber)
        .unitNumber(this.unitNumber)
        .usage(this.usage)
        .accessibility(this.accessibility)
        .constructionPrice(this.constructionPrice)
        .grossFloorPrice(this.grossFloorPrice)
        .preSaleConstructionArea(this.preSaleConstructionArea)
        .preSaleGrossFloorArea(this.preSaleGrossFloorArea)
        .preSaleSharedArea(this.preSaleSharedArea)
        .preCompletionConstructionArea(this.preCompletionConstructionArea)
        .preCompletionGrossFloorArea(this.preCompletionGrossFloorArea)
        .preCompletionSharedArea(this.preCompletionSharedArea)
        .build();
  }

  public static NewHouseUnitInfoDto of(NewHouseUnitInfoDO entity) {
    return NewHouseUnitInfoDto.builder()
        .id(entity.getId())
        .blockId(entity.getBlockId())
        .status(entity.getStatus())
        .blockNumber(entity.getBlockNumber())
        .floorNumber(entity.getFloorNumber())
        .unitSourceNumber(entity.getUnitSourceNumber())
        .unitNumber(entity.getUnitNumber())
        .usage(entity.getUsage())
        .accessibility(entity.getAccessibility())
        .constructionPrice(entity.getConstructionPrice())
        .grossFloorPrice(entity.getGrossFloorPrice())
        .preSaleConstructionArea(entity.getPreSaleConstructionArea())
        .preSaleGrossFloorArea(entity.getPreSaleGrossFloorArea())
        .preSaleSharedArea(entity.getPreSaleSharedArea())
        .preCompletionConstructionArea(entity.getPreCompletionConstructionArea())
        .preCompletionGrossFloorArea(entity.getPreCompletionGrossFloorArea())
        .preCompletionSharedArea(entity.getPreCompletionSharedArea())
        .build();
  }
}
