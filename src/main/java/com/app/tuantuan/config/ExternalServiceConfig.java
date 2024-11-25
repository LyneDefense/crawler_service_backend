package com.app.tuantuan.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
@Getter
public class ExternalServiceConfig {

  @Value("${external-services.http.backend-service.url}")
  private String backendServiceUrl;

  @Value("${external-services.http.backend-service.port}")
  private Integer backendServicePort;
}
