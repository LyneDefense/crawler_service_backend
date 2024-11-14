package com.app.tuantuan.service.caller.feignclient;

import com.app.tuantuan.model.dto.newhouse.SZNewHouseProjectDto;
import feign.Headers;
import feign.Param;
import feign.RequestLine;

public interface CrawlerUpdateServiceClient {

    @Headers("Content-Type: application/json")
    @RequestLine("POST /crawler/update")
    void updateCrawlerData(@Param("body") SZNewHouseProjectDto dto);
}
