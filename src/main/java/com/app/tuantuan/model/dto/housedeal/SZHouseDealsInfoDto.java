package com.app.tuantuan.model.dto.housedeal;

import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.entity.housedeal.SZHouseDealsArealDetailDO;
import com.app.tuantuan.model.entity.housedeal.SZHouseDealsDetailDO;
import com.app.tuantuan.model.entity.housedeal.SZHouseDealsInfoDO;
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
@ApiModel("商品房成交信息")
public class SZHouseDealsInfoDto {

  private String id;

  @ApiModelProperty("所在区")
  private SZDistrictEnum district;

  @ApiModelProperty("记录所在年份")
  private int year;

  @ApiModelProperty("记录所在月份")
  private int month;

  @ApiModelProperty("额外的信息")
  private List<String> messages;

  private LocalDate updateDate;

  @ApiModelProperty("商品房成交信息详情")
  private List<SZHouseDealsDetailDto> dealsDetails;

  @ApiModelProperty("商品住房按面积统计成交信息")
  private List<SZHouseDealsAreaDetailDto> dealsAreaDetails;

  public SZHouseDealsInfoDO to() {
    return SZHouseDealsInfoDO.builder()
        .id(this.id)
        .district(this.district)
        .year(this.year)
        .month(this.month)
        .updateDate(this.updateDate)
        .messages(this.messages)
        .build();
  }

  public static SZHouseDealsInfoDto of(
      SZHouseDealsInfoDO szHouseDealsInfoDO,
      List<SZHouseDealsDetailDO> houseDealsDetailDOS,
      List<SZHouseDealsArealDetailDO> SZHouseDealsArealDetailDOS) {
    return SZHouseDealsInfoDto.builder()
        .id(szHouseDealsInfoDO.getId())
        .district(szHouseDealsInfoDO.getDistrict())
        .year(szHouseDealsInfoDO.getYear())
        .month(szHouseDealsInfoDO.getMonth())
        .updateDate(szHouseDealsInfoDO.getUpdateDate())
        .messages(szHouseDealsInfoDO.getMessages())
        .dealsDetails(houseDealsDetailDOS.stream().map(SZHouseDealsDetailDto::of).toList())
        .dealsAreaDetails(
            SZHouseDealsArealDetailDOS.stream().map(SZHouseDealsAreaDetailDto::of).toList())
        .build();
  }

  @Override
  public String toString() {
    return "商品房成交信息{"
        + "城市="
        + "深圳"
        + ", 年份="
        + year
        + ", 月份="
        + month
        + ", 所在区='"
        + district
        + '\''
        + ", 商品房成交信息="
        + dealsDetails
        + ", 商品住房按面积统计成交信息="
        + dealsAreaDetails
        + '}';
  }
}
