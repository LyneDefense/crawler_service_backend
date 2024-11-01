package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.model.entity.newhouse.NewHouseMainPageItemDO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;

import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewHouseMainPageItemDto {

  private String id;

  @ApiModelProperty("预售证号")
  private String preSaleNumber;

  @ApiModelProperty("预售证号链接")
  private String preSaleNumberLink;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("项目名称链接")
  private String projectNameLink;

  @ApiModelProperty("开发商")
  private String developer;

  @ApiModelProperty("区域")
  private String district;

  @ApiModelProperty("批准日期")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate approvalDate;

  public NewHouseMainPageItemDO to() {
    return NewHouseMainPageItemDO.builder()
        .id(this.id)
        .preSaleNumber(this.preSaleNumber)
        .preSaleNumberLink(this.preSaleNumberLink)
        .projectName(this.projectName)
        .projectNameLink(this.projectNameLink)
        .developer(this.developer)
        .district(this.district)
        .approvalDate(this.approvalDate)
        .build();
  }

  public static NewHouseMainPageItemDto of(NewHouseMainPageItemDO entity) {
    return NewHouseMainPageItemDto.builder()
        .id(entity.getId())
        .preSaleNumber(entity.getPreSaleNumber())
        .preSaleNumberLink(entity.getPreSaleNumberLink())
        .projectName(entity.getProjectName())
        .projectNameLink(entity.getProjectNameLink())
        .developer(entity.getDeveloper())
        .district(entity.getDistrict())
        .approvalDate(entity.getApprovalDate())
        .build();
  }
}
