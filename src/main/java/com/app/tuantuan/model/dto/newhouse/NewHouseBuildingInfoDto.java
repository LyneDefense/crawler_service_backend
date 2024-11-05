package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.model.entity.newhouse.NewHouseBlockInfoDO;
import com.app.tuantuan.model.entity.newhouse.NewHouseBuildingInfoDO;
import com.app.tuantuan.model.entity.newhouse.NewHouseUnitInfoDO;
import io.swagger.annotations.ApiModel;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ApiModel("房源项目卖方信息-楼栋-楼栋详情信息")
public class NewHouseBuildingInfoDto {

  private String id;

  private String projectId;

  private String buildingName;

  private List<NewHouseBlockInfoDto> blockInfoList = new ArrayList<>();

  public NewHouseBuildingInfoDO to(String id, String projectId) {
    return NewHouseBuildingInfoDO.builder()
        .id(id)
        .projectId(projectId)
        .buildingName(this.buildingName)
        .build();
  }

  public static NewHouseBuildingInfoDto of(
      NewHouseBuildingInfoDO entity,
      List<NewHouseBlockInfoDO> blockInfoDOS,
      List<NewHouseUnitInfoDO> unitInfoDOS) {
    return NewHouseBuildingInfoDto.builder()
        .id(entity.getId())
        .projectId(entity.getProjectId())
        .buildingName(entity.getBuildingName())
        .blockInfoList(
            blockInfoDOS.stream()
                .map(blockInfoDO -> NewHouseBlockInfoDto.of(blockInfoDO, unitInfoDOS))
                .toList())
        .build();
  }
}
