package com.app.tuantuan.model.dto.newhouse;

import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewHouseSalesMixedInfoDto {

  private NewHouseSalesInfoDto salesInfo;

  private List<NewHouseSalesBuildingInfoDto> buildingInfos;
}
