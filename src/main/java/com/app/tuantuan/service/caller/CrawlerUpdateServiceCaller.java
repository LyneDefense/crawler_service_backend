package com.app.tuantuan.service.caller;

import com.app.tuantuan.config.ExternalServiceConfig;
import com.app.tuantuan.model.dto.newhouse.SZNewHouseProjectDto;
import com.app.tuantuan.service.caller.feignclient.CrawlerUpdateServiceClient;
import feign.Feign;
import feign.FeignException;
import feign.Request;
import feign.Retryer;
import feign.jackson.JacksonDecoder;
import feign.jackson.JacksonEncoder;
import javax.annotation.Resource;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class CrawlerUpdateServiceCaller extends BaseHttpServiceCaller {

  @Resource ExternalServiceConfig externalServiceConfig;

  private CrawlerUpdateServiceClient crawlerUpdateServiceClient;

  public static final int CAD_PARSER_CONNECTION_TIMEOUT = 10 * 1000;

  public static final int CAD_PARSER_HTTP_TIMEOUT = 60 * 1000;

  @Override
  void init() {
    final String baseUrl =
        String.format(
            "http://%s:%d",
            externalServiceConfig.getBackendServiceUrl(),
            externalServiceConfig.getBackendServicePort());
    log.info("[初始化CrawlerUpdateServiceCaller,baseUrl:{}]", baseUrl);
    this.crawlerUpdateServiceClient =
        new Feign.Builder()
            .retryer(Retryer.NEVER_RETRY)
            .options(new Request.Options(CAD_PARSER_CONNECTION_TIMEOUT, CAD_PARSER_HTTP_TIMEOUT))
            .decoder(new JacksonDecoder(objectMapper))
            .encoder(new JacksonEncoder(objectMapper))
            .target(CrawlerUpdateServiceClient.class, baseUrl);
  }

  @SneakyThrows
  public void updateCrawlerData(SZNewHouseProjectDto dto) {
    log.info("[请求更新后端的基地和一户一价数据,楼盘名称:{}]", dto.getProjectName());
    try {
      this.crawlerUpdateServiceClient.updateCrawlerData(dto);
    } catch (FeignException e) {
      log.error("[请求更新后端的基地和一户一价数据失败,楼盘名称:{},stackTrace:{}]", dto.getProjectName(), e.getMessage());
    }
  }
}
