package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.model.entity.newhouse.NewHouseBlockInfoDO;
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
@ApiModel("房源项目卖方信息-楼栋-'座'信息")
public class NewHouseBlockInfoDto {

  private String id;

  private String buildingId;

  private String blockName;

  private List<NewHouseUnitInfoDto> unitInfoList = new ArrayList<>();

  public NewHouseBlockInfoDO to(String id, String buildingId) {
    return NewHouseBlockInfoDO.builder()
        .id(id)
        .buildingId(buildingId)
        .blockName(this.blockName)
        .build();
  }

  public static NewHouseBlockInfoDto of(
      NewHouseBlockInfoDO entity, List<NewHouseUnitInfoDO> unitInfoList) {
    return NewHouseBlockInfoDto.builder()
        .id(entity.getId())
        .buildingId(entity.getBuildingId())
        .blockName(entity.getBlockName())
        .unitInfoList(unitInfoList.stream().map(NewHouseUnitInfoDto::of).toList())
        .build();
  }
}
