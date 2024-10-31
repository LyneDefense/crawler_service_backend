package com.app.tuantuan.model.dto.housedeal.used;

import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.entity.housedeal.used.SZUsedHouseDealsInfoDO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
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
@ApiModel("深圳二手商品住房成交信息")
public class SZUsedHouseDealsInfoDto {

  private String id;

  @ApiModelProperty("日期")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate date;

  private SZDistrictEnum district;

  private List<SZUsedHouseDealsDetailDto> details;

  public SZUsedHouseDealsInfoDO to() {
    return SZUsedHouseDealsInfoDO.builder()
        .id(this.id)
        .date(this.date)
        .district(this.district)
        .build();
  }

  public static SZUsedHouseDealsInfoDto of(
      SZUsedHouseDealsInfoDO entity, List<SZUsedHouseDealsDetailDto> details) {
    return SZUsedHouseDealsInfoDto.builder()
        .id(entity.getId())
        .date(entity.getDate())
        .district(entity.getDistrict())
        .details(details)
        .build();
  }
}
