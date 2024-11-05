package com.app.tuantuan.crawler.newhouse;

import cn.hutool.core.util.StrUtil;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.HouseStatus;
import com.app.tuantuan.model.dto.newhouse.NewHouseBlockInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseBuildingInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseSalesBuildingInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseUnitInfoDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.HttpRequestRetryStrategy;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.DefaultHttpRequestRetryStrategy;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.util.TimeValue;
import org.apache.hc.core5.util.Timeout;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewHouseBuildingInfoCrawler {

  private static final String BASE_URL = "https://zjj.sz.gov.cn/ris/bol/szfdc/";

  public NewHouseBuildingInfoDto crawl(NewHouseSalesBuildingInfoDto dto) {
    try {
      // 初始化HTTP客户端并设置CookieStore
      CookieStore cookieStore = new BasicCookieStore();
      RequestConfig requestConfig =
          RequestConfig.custom()
              .setConnectTimeout(Timeout.ofSeconds(30)) // 连接超时
              .setResponseTimeout(Timeout.ofSeconds(30)) // 响应超时
              .build();
      HttpRequestRetryStrategy retryStrategy =
          new DefaultHttpRequestRetryStrategy(3, TimeValue.ofSeconds(5));

      CloseableHttpClient httpClient =
          HttpClients.custom()
              .setDefaultRequestConfig(requestConfig)
              .setRetryStrategy(retryStrategy)
              .setDefaultCookieStore(cookieStore)
              .build();

      // 步骤1：获取初始页面
      HttpGet initialGet = new HttpGet(dto.getDetailLink());
      CloseableHttpResponse initialResponse = null;
      Document initialDoc;
      try {
        initialResponse = httpClient.execute(initialGet);
        String initialHtml =
            EntityUtils.toString(initialResponse.getEntity(), StandardCharsets.UTF_8);
        initialResponse.close();
        initialDoc = Jsoup.parse(initialHtml);
      } catch (IOException | ParseException e) {
        throw new CustomException("楼型详情解析失败");
      }
      log.info("[开始解析楼栋的具体数据,楼盘名称:{},楼栋名称:{}]", dto.getProjectName(), dto.getBuildingName());

      // 初始化DTO
      NewHouseBuildingInfoDto buildingInfoDto = new NewHouseBuildingInfoDto();
      buildingInfoDto.setBuildingName(dto.getBuildingName());

      // 步骤2：解析“座号”标签
      Elements branchLinks = initialDoc.select("#divShowBranch a");
      for (Element branchLink : branchLinks) {
        String branchName = branchLink.text().trim();
        String branchHref = branchLink.attr("href");

        NewHouseBlockInfoDto blockInfoDto = new NewHouseBlockInfoDto();
        blockInfoDto.setBlockName(branchName.replaceAll("[\\[\\]]", "")); // 去除方括号
        log.info("[当前解析到的座号为:{},开始解析该座下面的户型具体数据]", branchName);
        // 构建完整的URL
        String branchUrl = BASE_URL + branchHref;

        // 步骤3：访问“座号”页面
        HttpGet branchGet = new HttpGet(branchUrl);
        String branchHtml = null;
        CloseableHttpResponse branchResponse = null;
        branchResponse = httpClient.execute(branchGet);
        branchHtml = EntityUtils.toString(branchResponse.getEntity(), StandardCharsets.UTF_8);
        branchResponse.close();
        Document branchDoc = Jsoup.parse(branchHtml);

        // 步骤4：解析单元信息表格
        Elements tables = branchDoc.select("table.table.ta-c.table2");
        if (tables.isEmpty()) {
          System.out.println("未找到表格，请检查CSS选择器或HTML结构是否变化。");
          continue;
        }

        for (Element table : tables) {
          Elements rows = table.select("tr");
          int currentFloorNumber = 0; // 用于跟踪当前楼层号

          for (Element row : rows) {
            Elements cols = row.select("td");
            if (cols.isEmpty()) continue; // 跳过没有<td>的行

            int colIndex = 0; // 当前行中房号信息的起始索引

            // 检查第一列是否包含楼层信息
            String firstTdText = cols.get(0).text().trim();
            if (firstTdText.contains("层")) {
              currentFloorNumber = extractFloorNumber(firstTdText);
              colIndex = 1; // 房号信息从第二列开始
            }

            // 遍历房号列
            for (int i = colIndex; i < cols.size(); i++) {
              Element col = cols.get(i);

              // 尝试选择包含房号的特定<div>
              Elements houseNumberDivs = col.select("div.presale2Axihao");
              if (houseNumberDivs.isEmpty()) {
                // 如果特定的<div>不存在，尝试匹配包含 "房号：数字" 的<div>
                houseNumberDivs = col.select("div:matchesOwn(^房号：\\d+)");
              }

              if (!houseNumberDivs.isEmpty()) {
                // 提取房号文本并清理
                String houseNumberText = houseNumberDivs.first().text().trim().replace("房号：", "");

                // 提取房号对应的链接（详情页 URL）
                Elements links = col.select("a.presale2like[href]");
                if (links.isEmpty()) continue; // 跳过无链接的单元格

                Element link = links.first();
                String houseStatus = link.text().trim();
                String href = link.attr("href").trim();

                // 创建并填充 NewHouseUnitInfoDto 对象
                NewHouseUnitInfoDto unitInfoDto = new NewHouseUnitInfoDto();
                unitInfoDto.setStatus(HouseStatus.fromValue(houseStatus)); // 假设 HouseStatus 是枚举类型
                unitInfoDto.setBlockNumber(blockInfoDto.getBlockName());
                unitInfoDto.setFloorNumber(currentFloorNumber);
                unitInfoDto.setUnitSourceNumber(houseNumberText);
                unitInfoDto.setUnitNumber(
                    UnitNumberConvertor.convertUnitNumber(houseNumberText, currentFloorNumber));

                // 步骤5：抓取单元详细信息
                String unitDetailUrl = BASE_URL + href;
                unitInfoDto = fetchUnitDetail(httpClient, unitDetailUrl, unitInfoDto);

                // 添加到block信息中
                blockInfoDto.getUnitInfoList().add(unitInfoDto);
              }
            }
          }
        }

        // 添加到楼栋信息中
        buildingInfoDto.getBlockInfoList().add(blockInfoDto);
      }
      httpClient.close();
      return buildingInfoDto;
    } catch (Exception e) {
      throw new CustomException(StrUtil.format("楼型详情爬取失败,项目名称:{}", dto.getProjectName()), e);
    }
  }

  private int extractFloorNumber(String floorText) {
    if (floorText == null || floorText.isEmpty()) {
      return 0;
    }

    floorText = floorText.trim();

    boolean isNegative = false;
    if (floorText.startsWith("负")) {
      isNegative = true;
      floorText = floorText.substring(1); // 移除“负”
    }

    // 提取“层”之前的数字部分
    String numeralPart = floorText.split("层")[0];

    int floorNumber = 0;

    if (numeralPart.matches("\\d+")) { // 阿拉伯数字
      try {
        floorNumber = Integer.parseInt(numeralPart);
      } catch (NumberFormatException e) {
        log.error("无法解析楼层号: {}", floorText, e);
        return 0;
      }
    } else { // 中文数字
      floorNumber = chineseNumeralToInteger(numeralPart);
      if (floorNumber == -1) { // 表示解析失败
        log.error("无法解析楼层号: {}", floorText);
        return 0;
      }
    }

    return isNegative ? -floorNumber : floorNumber;
  }

  /**
   * 将中文数字转换为整数。支持1-99。 例如： "一" -> 1 "二" -> 2 "十" -> 10 "十一" -> 11 "二十" -> 20 "二十一" -> 21
   *
   * @param chineseNumeral 中文数字
   * @return 对应的整数，无法解析返回-1
   */
  private int chineseNumeralToInteger(String chineseNumeral) {
    Map<Character, Integer> cnNumbers = new HashMap<>();
    cnNumbers.put('零', 0);
    cnNumbers.put('一', 1);
    cnNumbers.put('二', 2);
    cnNumbers.put('三', 3);
    cnNumbers.put('四', 4);
    cnNumbers.put('五', 5);
    cnNumbers.put('六', 6);
    cnNumbers.put('七', 7);
    cnNumbers.put('八', 8);
    cnNumbers.put('九', 9);
    cnNumbers.put('十', 10);

    int result = 0;
    int temp = 0;
    boolean hasTen = false;

    for (int i = 0; i < chineseNumeral.length(); i++) {
      char c = chineseNumeral.charAt(i);
      if (cnNumbers.containsKey(c)) {
        int num = cnNumbers.get(c);
        if (c == '十') {
          if (i == 0) { // 例如 "十" 表示10
            temp = 10;
          } else {
            temp = (temp == 0 ? 1 : temp) * 10;
          }
          hasTen = true;
        } else {
          temp += num;
        }
      } else {
        // 无效字符
        return -1;
      }
    }

    result += temp;

    return result;
  }

  //  private int extractUnitSourceNumber(String houseNumberText) {
  //    // 示例："房号：3501" -> 3501
  //    try {
  //      return Integer.parseInt(houseNumberText.replaceAll("[^0-9]", ""));
  //    } catch (NumberFormatException e) {
  //      throw new CustomException("房号解析失败: " + houseNumberText);
  //    }
  //  }

  private NewHouseUnitInfoDto fetchUnitDetail(
      CloseableHttpClient httpClient, String url, NewHouseUnitInfoDto unitInfoDto) {
    HttpGet getDetail = new HttpGet(url);
    getDetail.setHeader(
        "User-Agent",
        "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/115.0.0.0 Safari/537.36");
    CloseableHttpResponse detailResponse = null;
    String detailHtml = null;
    try {
      detailResponse = httpClient.execute(getDetail);
      detailHtml = EntityUtils.toString(detailResponse.getEntity(), "UTF-8");
      detailResponse.close();
    } catch (IOException | ParseException e) {
      throw new CustomException("户型详情解析失败");
    }
    Document detailDoc = Jsoup.parse(detailHtml);

    // 步骤5.2：解析详细信息
    Elements tables = detailDoc.select("table.table.ta-c.table2.table-white");
    if (tables.isEmpty()) {
      log.warn("[未找到房号为:{} 的详细信息表格,请核查，跳过解析]", unitInfoDto.getUnitSourceNumber());
      return unitInfoDto;
    }

    // 假设只有一个表格
    Element table = tables.first();
    Elements rows = table.select("tr");

    String currentSection = "";

    for (Element row : rows) {
      // 首先检查是否有<th>标签
      Elements thElements = row.select("th");
      if (!thElements.isEmpty()) {
        // 检查是否为标题行
        Element header = thElements.first().selectFirst("h3");
        if (header != null) {
          String headerText = header.text().trim();
          if (headerText.contains("预售查丈")) {
            currentSection = "preSale";
          } else if (headerText.contains("竣工查丈")) {
            currentSection = "preCompletion";
          } else {
            currentSection = "";
          }
        }
        continue; // 处理完标题行后跳过
      }

      // 如果没有<th>，则处理<td>
      Elements tdElements = row.select("td");
      if (tdElements.size() < 2) {
        continue; // 跳过无效行
      }

      int i = 0;
      while (i < tdElements.size()) {
        String key = tdElements.get(i).text().trim();

        if (key.equals("拟售价格")) {
          // 处理“拟售价格”字段，有两个价格值
          if (i + 1 < tdElements.size()) {
            String value1 = tdElements.get(i + 1).html().trim();
            double constructionPrice = parsePrice(value1);
            unitInfoDto.setConstructionPrice(constructionPrice);
          } else {
            log.warn("没有找到‘拟售价格’对应的key: {}，请核查", key);
          }

          if (i + 2 < tdElements.size()) {
            String value2 = tdElements.get(i + 2).html().trim();
            double grossFloorPrice = parsePrice(value2);
            unitInfoDto.setGrossFloorPrice(grossFloorPrice);
          } else {
            log.warn("没有找到‘拟售价格-按套内面积计’对应的key: {}，请核查", key);
          }

          i += 3; // 跳过已处理的三个<td>
        } else {
          if (i + 1 >= tdElements.size()) {
            i += 1;
            continue;
          }

          String value = tdElements.get(i + 1).html().trim();

          switch (key) {
            case "用途":
              String usage = Jsoup.parse(value).text().trim();
              unitInfoDto.setUsage(usage);
              break;

            case "是否无障碍住房":
              String accessibilityText = Jsoup.parse(value).text().trim();
              Boolean accessibility =
                  "是".equals(accessibilityText)
                      ? Boolean.TRUE
                      : "否".equals(accessibilityText) ? false : null;
              unitInfoDto.setAccessibility(accessibility);
              break;

            case "建筑面积":
              if ("preSale".equals(currentSection)) {
                double preSaleConstructionArea = parseArea(value);
                unitInfoDto.setPreSaleConstructionArea(preSaleConstructionArea);
              } else if ("preCompletion".equals(currentSection)) {
                double preCompletionConstructionArea = parseArea(value);
                unitInfoDto.setPreCompletionConstructionArea(preCompletionConstructionArea);
              }
              break;

            case "套内建筑面积":
              if ("preSale".equals(currentSection)) {
                double preSaleGrossFloorArea = parseArea(value);
                unitInfoDto.setPreSaleGrossFloorArea(preSaleGrossFloorArea);
              } else if ("preCompletion".equals(currentSection)) {
                double preCompletionGrossFloorArea = parseArea(value);
                unitInfoDto.setPreCompletionGrossFloorArea(preCompletionGrossFloorArea);
              }
              break;

            case "分摊面积":
              if ("preSale".equals(currentSection)) {
                double preSaleSharedArea = parseArea(value);
                unitInfoDto.setPreSaleSharedArea(preSaleSharedArea);
              } else if ("preCompletion".equals(currentSection)) {
                double preCompletionSharedArea = parseArea(value);
                unitInfoDto.setPreCompletionSharedArea(preCompletionSharedArea);
              }
              break;
            default:
              break;
          }

          i += 2; // 跳过已处理的两个<td>
        }
      }
    }

    return unitInfoDto;
  }

  private double parsePrice(String priceText) {
    // 示例："132771元/平方米" -> 132771
    priceText = priceText.replaceAll("[^0-9.]", "");
    return priceText.isEmpty() ? 0.0 : Double.parseDouble(priceText);
  }

  private double parseArea(String areaText) {
    // 示例："370.06平方米" -> 370.06
    areaText = areaText.replaceAll("[^0-9.]", "");
    return areaText.isEmpty() ? 0.0 : Double.parseDouble(areaText);
  }
}
