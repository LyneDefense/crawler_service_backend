package com.app.tuantuan.model.dto.onlinesign.onsale;

import com.app.tuantuan.model.entity.onlinesign.onsale.SZOnsaleContractOnlineSignDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("商品房现售购房合同网签信息详情")
public class SZOnsaleContractOnlineSignDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("用途")
  private String category;

  @ApiModelProperty("网签面积")
  private Double salesArea;

  @ApiModelProperty("网签套数")
  private Integer salesCount;

  public SZOnsaleContractOnlineSignDetailDO to(String parentId) {
    return SZOnsaleContractOnlineSignDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .category(this.category)
        .salesArea(this.salesArea)
        .salesCount(this.salesCount)
        .build();
  }

  public static SZOnsaleContractOnlineSignDetailDto of(SZOnsaleContractOnlineSignDetailDO entity) {
    return SZOnsaleContractOnlineSignDetailDto.builder()
        .id(entity.getId())
        .parentId(entity.getParentId())
        .category(entity.getCategory())
        .salesArea(entity.getSalesArea())
        .salesCount(entity.getSalesCount())
        .build();
  }
}
