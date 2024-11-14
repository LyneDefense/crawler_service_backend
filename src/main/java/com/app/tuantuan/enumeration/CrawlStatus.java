package com.app.tuantuan.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum CrawlStatus {
  SKIPPED_NOT_RESIDENTIAL("SKIPPED_NOT_RESIDENTIAL"), // 跳过爬取，因为不是住宅项目
  SUCCESS("SUCCESS"), // 爬取并保存成功
  FAILURE("FAILURE"); // 爬取或保存失败

  @JsonValue
  private final String name;

  CrawlStatus(String name) {
    this.name = name;
  }
}
