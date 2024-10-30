package com.app.tuantuan.model.dto.statistic;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SZHouseDealsDataCrawlerReq {

    @JsonProperty("dateType")
    private String dateType;

    @JsonProperty("startDate")
    private String startDate;

    @JsonProperty("endDate")
    private String endDate;
}
