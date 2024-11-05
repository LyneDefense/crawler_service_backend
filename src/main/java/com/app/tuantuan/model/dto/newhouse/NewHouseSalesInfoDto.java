package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.model.entity.newhouse.NewHouseSalesInfoDO;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.time.LocalDate;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("房源项目卖方信息")
public class NewHouseSalesInfoDto {

  private String id;

  private String projectId;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("宗地号")
  private String plotNumber;

  @ApiModelProperty("项目位置")
  private String plotLocation;

  @ApiModelProperty("受让时间")
  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  private LocalDate transferDate;

  @ApiModelProperty("所在区域")
  private String district;

  @ApiModelProperty("权属来源")
  private String ownershipSource;

  @ApiModelProperty("批准机关")
  private String approvingAuthority;

  @ApiModelProperty("合同文号")
  private String contractNumber;

  @ApiModelProperty("使用年限")
  private String useYears;

  @ApiModelProperty("补充协议")
  private String supplementaryAgreement;

  @ApiModelProperty("用地规划许可证")
  private String landPlanningPermit;

  @ApiModelProperty("房屋用途")
  private String houseUsage;

  @ApiModelProperty("土地用途")
  private String landUsage;

  @ApiModelProperty("土地等级")
  private String landGrade;

  @ApiModelProperty("基地面积")
  private double baseArea;

  @ApiModelProperty("宗地面积")
  private double plotArea;

  @ApiModelProperty("总建筑面积")
  private double totalConstructArea;

  @ApiModelProperty("预售总套数")
  private int presaleTotalSets;

  @ApiModelProperty("预售总面积")
  private double presaleTotalArea;

  @ApiModelProperty("现售总套数")
  private int onsaleTotalSets;

  @ApiModelProperty("现售总面积")
  private double onsaleTotalArea;

  @ApiModelProperty("工程监管机构")
  private String engineeringSupervisionAgency;

  @ApiModelProperty("物业管理公司")
  private String propertyManagementCompany;

  @ApiModelProperty("物业费")
  private String managementFee;

  public NewHouseSalesInfoDO to(String id, String projectId) {
    return NewHouseSalesInfoDO.builder()
        .id(id)
        .projectId(projectId)
        .projectName(this.projectName)
        .plotNumber(this.plotNumber)
        .plotLocation(this.plotLocation)
        .transferDate(this.transferDate)
        .district(this.district)
        .ownershipSource(this.ownershipSource)
        .approvingAuthority(this.approvingAuthority)
        .contractNumber(this.contractNumber)
        .useYears(this.useYears)
        .supplementaryAgreement(this.supplementaryAgreement)
        .landPlanningPermit(this.landPlanningPermit)
        .houseUsage(this.houseUsage)
        .landUsage(this.landUsage)
        .landGrade(this.landGrade)
        .baseArea(this.baseArea)
        .plotArea(this.plotArea)
        .totalConstructArea(this.totalConstructArea)
        .presaleTotalSets(this.presaleTotalSets)
        .presaleTotalArea(this.presaleTotalArea)
        .onsaleTotalSets(this.onsaleTotalSets)
        .onsaleTotalArea(this.onsaleTotalArea)
        .engineeringSupervisionAgency(this.engineeringSupervisionAgency)
        .propertyManagementCompany(this.propertyManagementCompany)
        .managementFee(this.managementFee)
        .build();
  }

  public static NewHouseSalesInfoDto of(NewHouseSalesInfoDO entity) {
    return NewHouseSalesInfoDto.builder()
        .id(entity.getId())
        .projectId(entity.getProjectId())
        .projectName(entity.getProjectName())
        .plotNumber(entity.getPlotNumber())
        .plotLocation(entity.getPlotLocation())
        .transferDate(entity.getTransferDate())
        .district(entity.getDistrict())
        .ownershipSource(entity.getOwnershipSource())
        .approvingAuthority(entity.getApprovingAuthority())
        .contractNumber(entity.getContractNumber())
        .useYears(entity.getUseYears())
        .supplementaryAgreement(entity.getSupplementaryAgreement())
        .landPlanningPermit(entity.getLandPlanningPermit())
        .houseUsage(entity.getHouseUsage())
        .landUsage(entity.getLandUsage())
        .landGrade(entity.getLandGrade())
        .baseArea(entity.getBaseArea())
        .plotArea(entity.getPlotArea())
        .totalConstructArea(entity.getTotalConstructArea())
        .presaleTotalSets(entity.getPresaleTotalSets())
        .presaleTotalArea(entity.getPresaleTotalArea())
        .onsaleTotalSets(entity.getOnsaleTotalSets())
        .onsaleTotalArea(entity.getOnsaleTotalArea())
        .engineeringSupervisionAgency(entity.getEngineeringSupervisionAgency())
        .propertyManagementCompany(entity.getPropertyManagementCompany())
        .managementFee(entity.getManagementFee())
        .build();
  }
}
