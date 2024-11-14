package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.enumeration.CrawlStatus;
import io.swagger.annotations.ApiModelProperty;
import java.util.List;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SZNewHouseProjectDto {

  private CrawlStatus status;

  @ApiModelProperty("项目名称")
  private String projectName;

  @ApiModelProperty("项目信息")
  private NewHouseMainPageItemDto mainPageItem;

  @ApiModelProperty("楼栋信息列表")
  private List<NewHouseBuildingInfoDto> buildings;

  @ApiModelProperty("房源信息")
  private NewHouseSalesInfoDto salesInfo;
}
