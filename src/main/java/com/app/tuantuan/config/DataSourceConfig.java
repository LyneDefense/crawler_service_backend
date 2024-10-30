package com.app.tuantuan.config;

import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "spring.datasource")
public class DataSourceConfig implements CommandLineRunner {

  private static final Logger logger = LoggerFactory.getLogger(DataSourceConfig.class);

  private String url;
  private String driverClassName;

  @Override
  public void run(String... args) throws Exception {
    logger.info("正在连接的数据库 URL：{}", url);

    // 提取数据库名称
    String dbName = extractDatabaseName(url);
    logger.info("连接的数据库名称：{}", dbName);
  }

  private String extractDatabaseName(String url) {
    if (url == null || url.isEmpty()) {
      return "未知数据库";
    }
    // 假设数据库名称位于最后一个斜杠后
    int lastSlash = url.lastIndexOf('/');
    if (lastSlash == -1 || lastSlash == url.length() - 1) {
      return "未知数据库";
    }
    return url.substring(lastSlash + 1);
  }
}
