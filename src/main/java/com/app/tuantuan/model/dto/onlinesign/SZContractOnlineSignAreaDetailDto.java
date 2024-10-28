package com.app.tuantuan.model.dto.onlinesign;

import com.app.tuantuan.model.entity.onlinesign.SZContractOnlineSignAreaDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商品住房按面积统计购房合同网签信息")
public class SZContractOnlineSignAreaDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("面积区间")
  private String areaRange;

  @ApiModelProperty("购房合同网签套数")
  private int contractOnlineSignCount;

  @ApiModelProperty("购房合同网签面积")
  private double contractOnlineSignArea;

  public SZContractOnlineSignAreaDetailDto(
      String areaRange, int contractOnlineSignCount, double contractOnlineSignArea) {
    this.areaRange = areaRange;
    this.contractOnlineSignCount = contractOnlineSignCount;
    this.contractOnlineSignArea = contractOnlineSignArea;
  }

  public SZContractOnlineSignAreaDetailDO to(String parentId) {
    return SZContractOnlineSignAreaDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .areaRange(this.areaRange)
        .contractOnlineSignCount(this.contractOnlineSignCount)
        .contractOnlineSignArea(this.contractOnlineSignArea)
        .build();
  }

  public static SZContractOnlineSignAreaDetailDto of(SZContractOnlineSignAreaDetailDO entity) {
    return SZContractOnlineSignAreaDetailDto.builder()
        .id(entity.getId())
        .parentId(entity.getParentId())
        .areaRange(entity.getAreaRange())
        .contractOnlineSignCount(entity.getContractOnlineSignCount())
        .contractOnlineSignArea(entity.getContractOnlineSignArea())
        .build();
  }
}
