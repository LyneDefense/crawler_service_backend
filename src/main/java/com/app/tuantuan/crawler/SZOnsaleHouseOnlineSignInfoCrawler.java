package com.app.tuantuan.crawler;

import com.app.tuantuan.component.ObjectMapperFactory;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignDetailDto;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignDetailReq;
import com.app.tuantuan.model.dto.onlinesign.onsale.SZOnsaleContractOnlineSignInfoDto;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.entity.EntityBuilder;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.ContentType;
import org.springframework.stereotype.Component;

/** <a href="https://zjj.sz.gov.cn:8004/">...</a> - 一手放成交信息-现售成交-当日网签信息 */
@Slf4j
@Component
public class SZOnsaleHouseOnlineSignInfoCrawler {

  private static final String POST_DATE_URL =
      "https://zjj.sz.gov.cn:8004/api/marketInfoShow/getXsCjxxGsData";
  private static final String POST_ZONE_URL =
      "https://zjj.sz.gov.cn:8004/api/marketInfoShow/getXsInfoByZone";

  private final ObjectMapper objectMapper = ObjectMapperFactory.create();

  /**
   * 获取所有区域的销售详情列表
   *
   * @return List of SZOnsaleContractOnlineSignDetailDto
   */
  public List<SZOnsaleContractOnlineSignInfoDto> crawl() {
    LocalDate date = getXmlDateDay();
    List<SZOnsaleContractOnlineSignInfoDto> onlineSignInfoDtos = new ArrayList<>();
    for (SZDistrictEnum district : SZDistrictEnum.values()) {
      if (district == SZDistrictEnum.UNKNOWN) {
        continue;
      }
      log.info("[解析现售成交-当日网签信息,日期:{},区域:{}]", date, district.getValue());
      List<JsonNode> dataList;
      try {
        dataList = getXsInfoByZone(district);
      } catch (IOException e) {
        log.error("[解析现售成交-当日网签信息失败,日期:{},区域:{}]", date, district.getValue());
        continue;
      }
      List<SZOnsaleContractOnlineSignDetailDto> resultList = new ArrayList<>();
      for (JsonNode dataNode : dataList) {
        String useage = dataNode.path("useage").asText();
        String wqmjStr = dataNode.path("wqmj").asText();
        String wqtsStr = dataNode.path("wqts").asText();

        Double salesArea = parseDouble(wqmjStr);
        int salesCount = parseInt(wqtsStr);

        SZOnsaleContractOnlineSignDetailDto dto =
            new SZOnsaleContractOnlineSignDetailDto(null, null, useage, salesArea, salesCount);

        resultList.add(dto);
      }
      onlineSignInfoDtos.add(
          new SZOnsaleContractOnlineSignInfoDto(null, district, date, resultList));
    }

    return onlineSignInfoDtos;
  }

  /**
   * 获取xmlDateDay并转换为LocalDate
   *
   * @return LocalDate对象
   */
  public LocalDate getXmlDateDay() {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost postRequest = new HttpPost(POST_DATE_URL);
      postRequest.setHeader("Content-Type", "application/json");
      log.info("[开始解析现售成交-当日网签信息的日期]");
      LocalDate date = httpClient.execute(postRequest, this::handleGetDateResponse);
      log.info("[解析到现售成交-当日网签信息的日期:{}]", date);
      return date;
    } catch (IOException e) {
      throw new CustomException("[解析现售成交-当日网签信息的日期失败]");
    }
  }

  /**
   * 处理GET请求的HTTP响应，解析JSON并提取xmlDateDay
   *
   * @param response HTTP响应
   * @return 转换后的LocalDate
   * @throws IOException 如果JSON解析失败
   */
  private LocalDate handleGetDateResponse(ClassicHttpResponse response) throws IOException {
    int statusCode = response.getCode();
    if (statusCode >= 200 && statusCode < 300) {
      JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
      JsonNode xmlDateDayNode = rootNode.path("data").path("xmlDateDay");
      if (!xmlDateDayNode.isMissingNode()) {
        String xmlDateDayStr = xmlDateDayNode.asText();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月dd日");
        return LocalDate.parse(xmlDateDayStr, formatter);
      } else {
        throw new CustomException("[解析到现售成交-当日网签信息，xmlDateDay数据为空]");
      }
    } else {
      throw new CustomException("[解析现售成交-当日网签信息的日期失败，status:" + statusCode + "]");
    }
  }

  /**
   * 发送POST请求获取指定区域的销售信息
   *
   * @param district 区域枚举
   * @return List of JsonNode representing data
   * @throws IOException 如果HTTP请求或JSON解析失败
   */
  public List<JsonNode> getXsInfoByZone(SZDistrictEnum district) throws IOException {
    try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
      HttpPost postRequest = new HttpPost(POST_ZONE_URL);
      postRequest.setHeader("Content-Type", "application/json");

      String jsonBody =
          objectMapper.writeValueAsString(
              new SZOnsaleContractOnlineSignDetailReq(district.getValue()));
      postRequest.setEntity(
          EntityBuilder.create()
              .setText(jsonBody)
              .setContentType(ContentType.APPLICATION_JSON)
              .build());
      return httpClient.execute(postRequest, this::handlePostZoneResponse);
    }
  }

  /**
   * 处理POST请求的HTTP响应，解析JSON并提取data部分
   *
   * @param response HTTP响应
   * @return List of JsonNode representing data
   * @throws IOException 如果JSON解析失败
   */
  private List<JsonNode> handlePostZoneResponse(ClassicHttpResponse response) throws IOException {
    int statusCode = response.getCode();
    if (statusCode >= 200 && statusCode < 300) {
      JsonNode rootNode = objectMapper.readTree(response.getEntity().getContent());
      JsonNode dataNode = rootNode.path("data").path("data");
      if (dataNode.isArray()) {
        List<JsonNode> dataList = new ArrayList<>();
        dataNode.forEach(dataList::add);
        return dataList;
      } else {
        throw new CustomException("[解析现售成交-当日网签信息失败，date数据不是Array]");
      }
    } else {
      throw new CustomException("[解析现售成交-当日网签信息失败，status:" + statusCode + "]");
    }
  }

  /**
   * 辅助方法：解析字符串为Double
   *
   * @param value 字符串值
   * @return Double值，解析失败则返回0.0
   */
  private Double parseDouble(String value) {
    try {
      return Double.parseDouble(value);
    } catch (NumberFormatException e) {
      throw new CustomException("[解析字符串为Double失败，value:" + value + "]");
    }
  }

  /**
   * 辅助方法：解析字符串为int
   *
   * @param value 字符串值
   * @return int值，解析失败则返回0
   */
  private Integer parseInt(String value) {
    try {
      return Integer.parseInt(value);
    } catch (NumberFormatException e) {
      throw new CustomException("[解析字符串为int失败，value:" + value + "]");
    }
  }
}
