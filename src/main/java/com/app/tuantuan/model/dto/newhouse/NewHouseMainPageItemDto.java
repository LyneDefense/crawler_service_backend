package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.model.entity.newhouse.NewHouseMainPageItemDO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewHouseMainPageItemDto {

  @ApiModelProperty("projectId")
  @NotNull
  private String id;

  @ApiModelProperty("预售证号")
  @NotNull
  private String preSaleNumber;

  @ApiModelProperty("预售证号链接")
  private String preSaleNumberLink;

  @ApiModelProperty("项目名称")
  @NotNull
  private String projectName;

  @ApiModelProperty("项目名称链接")
  @NotNull
  private String projectNameLink;

  @ApiModelProperty("开发商")
  private String developer;

  @ApiModelProperty("区域")
  @NotNull
  private String district;

  @ApiModelProperty("批准日期")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @NotNull
  private LocalDate approvalDate;

  public NewHouseMainPageItemDO to(String id) {
    return NewHouseMainPageItemDO.builder()
        .id(id)
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
    if (entity == null) {
      return null;
    }
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
