package com.app.tuantuan.model.dto.onlinesign;

import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.entity.onlinesign.SZContractOnlineSignAreaDetailDO;
import com.app.tuantuan.model.entity.onlinesign.SZContractOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.SZSubscriptionOnlineSignDetailDO;
import com.app.tuantuan.model.entity.onlinesign.SZSubscriptionOnlineSignInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("深圳商品房网签认购信息")
public class SZSubscriptionOnlineSignInfoDto {

  private String id;

  @ApiModelProperty("所在区")
  private SZDistrictEnum district;

  @ApiModelProperty("日期")
  private LocalDate date;

  @ApiModelProperty("认购网签信息")
  private List<SZSubscriptionOnlineSignDetailDto> subscriptionDetails;

  @ApiModelProperty("商品房购房合同网签信息")
  private List<SZContractOnlineSignDetailDto> contractOnlineSignDetails;

  @ApiModelProperty("商品住房按面积统计购房合同网签信息")
  private List<SZContractOnlineSignAreaDetailDto> contractOnlineSignAreaDetails;

  public SZSubscriptionOnlineSignInfoDto(
      SZDistrictEnum district,
      LocalDate date,
      List<SZSubscriptionOnlineSignDetailDto> subscriptionDetails,
      List<SZContractOnlineSignDetailDto> contractOnlineSignDetails,
      List<SZContractOnlineSignAreaDetailDto> contractOnlineSignAreaDetails) {
    this.district = district;
    this.date = date;
    this.subscriptionDetails = subscriptionDetails;
    this.contractOnlineSignDetails = contractOnlineSignDetails;
    this.contractOnlineSignAreaDetails = contractOnlineSignAreaDetails;
  }

  public SZSubscriptionOnlineSignInfoDO to() {
    return SZSubscriptionOnlineSignInfoDO.builder()
        .id(this.id)
        .district(this.district)
        .date(this.date)
        .build();
  }

  public static SZSubscriptionOnlineSignInfoDto of(
      SZSubscriptionOnlineSignInfoDO entity,
      List<SZSubscriptionOnlineSignDetailDO> subscriptionDetails,
      List<SZContractOnlineSignDetailDO> contractOnlineSignDetails,
      List<SZContractOnlineSignAreaDetailDO> contractOnlineSignAreaDetails) {
    return SZSubscriptionOnlineSignInfoDto.builder()
        .id(entity.getId())
        .district(entity.getDistrict())
        .date(entity.getDate())
        .subscriptionDetails(
            subscriptionDetails.stream().map(SZSubscriptionOnlineSignDetailDto::of).toList())
        .contractOnlineSignDetails(
            contractOnlineSignDetails.stream().map(SZContractOnlineSignDetailDto::of).toList())
        .contractOnlineSignAreaDetails(
            contractOnlineSignAreaDetails.stream()
                .map(SZContractOnlineSignAreaDetailDto::of)
                .toList())
        .build();
  }
}
