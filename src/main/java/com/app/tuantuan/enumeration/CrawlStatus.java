package com.app.tuantuan.enumeration;

public enum CrawlStatus {
  SKIPPED_NOT_RESIDENTIAL, // 跳过爬取，因为不是住宅项目
  SUCCESS, // 爬取并保存成功
  FAILURE // 爬取或保存失败
}
