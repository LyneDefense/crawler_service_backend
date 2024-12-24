package com.app.tuantuan.model.dto.onlinesign.presale;

import com.app.tuantuan.model.entity.onlinesign.preale.SZSubscriptionOnlineSignDetailDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("认购网签信息")
public class SZSubscriptionOnlineSignDetailDto {

  private String id;

  private String parentId;

  @ApiModelProperty("用途")
  private String category;

  @ApiModelProperty("认购网签套数")
  private int subscriptionCount;

  @ApiModelProperty("认购网签面积")
  private double subscriptionArea;

  public SZSubscriptionOnlineSignDetailDto(
      String category, int subscriptionCount, double subscriptionArea) {
    this.category = category;
    this.subscriptionCount = subscriptionCount;
    this.subscriptionArea = subscriptionArea;
  }

  public SZSubscriptionOnlineSignDetailDO to(String parentId) {
    return SZSubscriptionOnlineSignDetailDO.builder()
        .id(this.id)
        .parentId(parentId)
        .category(this.category)
        .subscriptionCount(this.subscriptionCount)
        .subscriptionArea(this.subscriptionArea)
        .build();
  }

  public static SZSubscriptionOnlineSignDetailDto of(SZSubscriptionOnlineSignDetailDO entity) {
    return SZSubscriptionOnlineSignDetailDto.builder()
        .id(entity.getId())
        .parentId(entity.getParentId())
        .category(entity.getCategory())
        .subscriptionCount(entity.getSubscriptionCount())
        .subscriptionArea(entity.getSubscriptionArea())
        .build();
  }

  public boolean isResidenceOnlineSign(){
    return "住宅".equals(this.category);
  }
}
