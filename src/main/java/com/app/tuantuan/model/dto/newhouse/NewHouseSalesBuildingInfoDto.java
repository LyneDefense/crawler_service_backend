package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.model.entity.newhouse.NewHouseSalesBuildingInfoDO;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("房源项目卖方信息-楼栋信息")
public class NewHouseSalesBuildingInfoDto {

  private String id;

  private String projectId;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("楼名")
  private String buildingName;

  @ApiModelProperty("建设工程规划许可证")
  private String engineeringPlanningPermit;

  @ApiModelProperty("建筑工程施工许可证")
  private String engineeringConstructionPermit;

  @ApiModelProperty("套房信息的链接")
  private String detailLink;

  public NewHouseSalesBuildingInfoDO to(String id, String projectId) {
    return NewHouseSalesBuildingInfoDO.builder()
        .id(id)
        .projectId(projectId)
        .projectName(this.projectName)
        .buildingName(this.buildingName)
        .engineeringPlanningPermit(this.engineeringPlanningPermit)
        .engineeringConstructionPermit(this.engineeringConstructionPermit)
        .detailLink(this.detailLink)
        .build();
  }

  public static NewHouseSalesBuildingInfoDto of(NewHouseSalesBuildingInfoDO entity) {
    return NewHouseSalesBuildingInfoDto.builder()
        .id(entity.getId())
        .projectId(entity.getProjectId())
        .projectName(entity.getProjectName())
        .buildingName(entity.getBuildingName())
        .engineeringPlanningPermit(entity.getEngineeringPlanningPermit())
        .engineeringConstructionPermit(entity.getEngineeringConstructionPermit())
        .detailLink(entity.getDetailLink())
        .build();
  }
}
