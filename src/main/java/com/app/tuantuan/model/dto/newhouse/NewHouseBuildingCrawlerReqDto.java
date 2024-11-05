package com.app.tuantuan.model.dto.newhouse;

import java.util.List;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewHouseBuildingCrawlerReqDto {

  @NotNull @NotEmpty private List<NewHouseMainPageItemDto> maiPageItems;
}
