package com.app.tuantuan.model.dto.onlinesign.onsale;

import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.entity.onlinesign.onsale.SZOnsaleContractOnlineSignInfoDO;
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
@ApiModel("商品房现售购房合同网签信息")
public class SZOnsaleContractOnlineSignInfoDto {

  private String id;

  private SZDistrictEnum district;

  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate date;

  @ApiModelProperty("商品房现售购房合同网签信息详情列表")
  private List<SZOnsaleContractOnlineSignDetailDto> onsaleDetails;

  public SZOnsaleContractOnlineSignInfoDO to() {
    return SZOnsaleContractOnlineSignInfoDO.builder()
        .id(this.id)
        .district(this.district)
        .date(this.date)
        .build();
  }

  public static SZOnsaleContractOnlineSignInfoDto of(
      SZOnsaleContractOnlineSignInfoDO entity,
      List<SZOnsaleContractOnlineSignDetailDto> onsaleDetails) {
    return SZOnsaleContractOnlineSignInfoDto.builder()
        .id(entity.getId())
        .district(entity.getDistrict())
        .date(entity.getDate())
        .onsaleDetails(onsaleDetails)
        .build();
  }
}
