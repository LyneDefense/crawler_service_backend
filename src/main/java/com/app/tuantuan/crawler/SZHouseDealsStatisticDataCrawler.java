package com.app.tuantuan.crawler;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.model.dto.statistic.SZHouseDealStatisticDataDto;
import com.app.tuantuan.model.dto.statistic.SZHouseDealsDataCrawlerReq;
import com.app.tuantuan.model.dto.statistic.SZHouseDealsDataCrawlerSource;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.springframework.stereotype.Component;

/** 房地产成交趋势爬取 */
@Slf4j
@Component
public class SZHouseDealsStatisticDataCrawler {

  private static final String URL = "https://zjj.sz.gov.cn:8004/api/marketInfoShow/getFjzsInfoData";

  /**
   * 发送 POST 请求到指定 URL，获取附着信息数据。
   *
   * @param startDate 开始日期
   * @param endDate 结束日期
   * @return 服务器响应的 JSON 字符串
   * @throws IOException 如果发生 I/O 错误
   */
  public List<SZHouseDealStatisticDataDto> crawl(LocalDate startDate, LocalDate endDate) {
    // 创建 HTTP 客户端
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      // 创建 POST 请求
      HttpPost httpPost = new HttpPost(URL);
      httpPost.addHeader("Content-Type", "application/json");

      // 构建请求体
      SZHouseDealsDataCrawlerReq requestBody =
          new SZHouseDealsDataCrawlerReq("", startDate.toString(), endDate.toString());
      ObjectMapper objectMapper = new ObjectMapper();
      String json = objectMapper.writeValueAsString(requestBody);
      StringEntity entity = new StringEntity(json);
      httpPost.setEntity(entity);

      // 执行请求
      log.info("[开始爬取房地产成交趋势数据，起始日期:{},终止日期:{}]", startDate, endDate);
      try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
        int statusCode = response.getCode();
        if (statusCode >= 200 && statusCode < 300) {
          // 读取响应体
          JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
          JsonNode dataNode = rootNode.get("data");
          if (dataNode != null && !dataNode.isNull()) {
            SZHouseDealsDataCrawlerSource source =
                objectMapper.treeToValue(dataNode, SZHouseDealsDataCrawlerSource.class);
            List<SZHouseDealStatisticDataDto> data = this.translate(source);
            log.info("[爬取房地产成交趋势数据结束]");
            return data;
          } else {
            throw new CustomException("[深圳房地产成交趋势爬取-原始接口返回不包含data信息");
          }
        } else {
          throw new CustomException("[深圳房地产成交趋势爬取失败");
        }
      }
    } catch (IOException e) {
      throw new CustomException("[深圳房地产成交趋势爬取失败");
    }
  }

  public List<SZHouseDealStatisticDataDto> translate(SZHouseDealsDataCrawlerSource source) {
    if (source == null) {
      return List.of();
    }
    List<SZHouseDealStatisticDataDto> statisticDataList = new ArrayList<>();
    for (int i = 0; i < source.getDate().size(); i++) {
      LocalDate localDate =
          LocalDate.parse(source.getDate().get(i), DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      statisticDataList.add(
          SZHouseDealStatisticDataDto.builder()
              .date(localDate)
              .newHouseDealSetCount(source.getYsfTotalTs().get(i))
              .newHouseDealArea(source.getYsfDealArea().get(i))
              .usedHouseDealCount(source.getEsfTotalTs().get(i))
              .usedHouseDealArea(source.getEsfDealArea().get(i))
              .build());
    }
    return statisticDataList;
  }
}
