package com.app.tuantuan.model.dto.newhouse;

import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.base.PageParam;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import javax.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NewHouseMainPageReqDto extends PageParam {

  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate startDate;


  @JsonDeserialize(using = LocalDateDeserializer.class)
  @JsonSerialize(using = LocalDateSerializer.class)
  @JsonFormat(pattern = "yyyy-MM-dd")
  private LocalDate endDate;

  private String projectName;

  private SZDistrictEnum district;

  public NewHouseMainPageReqDto(LocalDate startDate, LocalDate endDate) {
    this.startDate = startDate;
    this.endDate = endDate;
  }
}
