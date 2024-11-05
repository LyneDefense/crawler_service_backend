package com.app.tuantuan.crawler.newhouse;

import cn.hutool.core.util.StrUtil;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseSalesBuildingInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseSalesInfoDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseSalesMixedInfoDto;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.ParseException;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewHouseSalesInfoCrawler {

  /**
   * 爬取项目详细页面的信息。
   *
   * @param dto 项目详细页面的URL
   * @return 包含项目卖方信息和楼栋信息的结果
   */
  public NewHouseSalesMixedInfoDto crawl(NewHouseMainPageItemDto dto) {
    try {
      CookieStore cookieStore = new BasicCookieStore();
      CloseableHttpClient httpClient =
          HttpClients.custom().setDefaultCookieStore(cookieStore).build();

      HttpClientContext context = HttpClientContext.create();
      HttpGet getRequest = new HttpGet(dto.getProjectNameLink());
      String html;
      try (var response = httpClient.execute(getRequest, context)) {
        html = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
      } catch (IOException | ParseException e) {
        throw new RuntimeException(e);
      }

      Document doc = Jsoup.parse(html, dto.getProjectNameLink());
      log.info("[开始爬取项目详情页卖方信息,项目名称:{}]", dto.getProjectName());
      NewHouseSalesInfoDto salesInfo = parseSalesInfo(doc);
      log.info("[开始爬取项目详情页卖方-楼栋链接信息,项目名称:{}]", dto.getProjectName());
      List<NewHouseSalesBuildingInfoDto> buildingInfos = parseBuildingInfo(doc);
      log.info(
          "[开始爬取项目详情页卖方-楼栋链接信息结束,项目名称:{}，楼栋信息:{}]",
          dto.getProjectName(),
          buildingInfos.stream().map(NewHouseSalesBuildingInfoDto::getBuildingName).toList());
      return new NewHouseSalesMixedInfoDto(salesInfo, buildingInfos);
    } catch (Exception e) {
      throw new CustomException(
          StrUtil.format("详情页面爬取失败，项目名称:{},预售证号:{}", dto.getProjectName(), dto.getPreSaleNumber()),
          e);
    }
  }

  /**
   * 解析项目卖方信息表格。
   *
   * @param doc 项目详细页面的Jsoup Document
   * @return NewHouseSalesInfoDto 对象
   */
  private NewHouseSalesInfoDto parseSalesInfo(Document doc) {
    NewHouseSalesInfoDto salesInfo = new NewHouseSalesInfoDto();

    // 选择第一个表格
    Element table = doc.selectFirst("table.table.ta-c.table2.table-white");
    if (table != null) {
      Elements rows = table.select("tr");
      for (Element row : rows) {
        Elements cols = row.select("td");
        if (cols.isEmpty()) continue; // 跳过空行

        // 遍历每行中的单元格，按键值对提取信息
        for (int i = 0; i < cols.size(); ) {
          String header = cols.get(i).text().trim();
          i++;
          if (i >= cols.size()) break; // 防止越界

          String value = cols.get(i).text().trim();
          i++;

          // 处理 colspan 属性，合并多列
          if (i < cols.size() && cols.get(i - 1).hasAttr("colspan")) {
            // 根据colspan属性调整i的值
            int colspan = Integer.parseInt(cols.get(i - 1).attr("colspan"));
            if (colspan > 1) {
              // 如果colspan > 1，则下一个单元格可能不对应
              // 这里假设每个header后面跟随一个或多个值，根据实际情况调整
              // 目前示例中colspan=3，只取第一个值
              value = cols.get(i - 1).text().trim().split(";")[0];
            }
          }

          // 根据header设置对应的字段
          switch (header) {
            case "项目名称":
              salesInfo.setProjectName(value);
              break;
            case "宗地号":
              salesInfo.setPlotNumber(value);
              break;
            case "宗地位置":
              salesInfo.setPlotLocation(value);
              break;
            case "受让日期":
              salesInfo.setTransferDate(parseDate(value));
              break;
            case "所在区域":
              salesInfo.setDistrict(value);
              break;
            case "权属来源":
              salesInfo.setOwnershipSource(value);
              break;
            case "批准机关":
              salesInfo.setApprovingAuthority(value);
              break;
            case "合同文号":
              salesInfo.setContractNumber(value);
              break;
            case "使用年限":
              salesInfo.setUseYears(value);
              break;
            case "补充协议":
              salesInfo.setSupplementaryAgreement(value);
              break;
            case "用地规划许可证":
              salesInfo.setLandPlanningPermit(value);
              break;
            case "房屋用途":
              salesInfo.setHouseUsage(value);
              break;
            case "土地用途":
              salesInfo.setLandUsage(value);
              break;
            case "土地等级":
              salesInfo.setLandGrade(value);
              break;
            case "基地面积":
              salesInfo.setBaseArea(parseDouble(value));
              break;
            case "宗地面积":
              salesInfo.setPlotArea(parseDouble(value));
              break;
            case "总建筑面积":
              salesInfo.setTotalConstructArea(parseDouble(value));
              break;
            case "预售总套数":
              salesInfo.setPresaleTotalSets(parseInt(value));
              break;
            case "预售总面积":
              salesInfo.setPresaleTotalArea(parseDouble(value));
              break;
            case "现售总套数":
              salesInfo.setOnsaleTotalSets(parseInt(value));
              break;
            case "现售总面积":
              salesInfo.setOnsaleTotalArea(parseDouble(value));
              break;
            case "工程监管机构":
              salesInfo.setEngineeringSupervisionAgency(value);
              break;
            case "物业管理公司":
              salesInfo.setPropertyManagementCompany(value);
              break;
            case "管理费":
              salesInfo.setManagementFee(value);
              break;
            default:
              // 处理未匹配的字段
              break;
          }
        }
      }
    }

    return salesInfo;
  }

  /**
   * 解析楼栋信息表格。
   *
   * @param doc 项目详细页面的Jsoup Document
   * @return List<NewHouseSalesBuildingInfoDto>
   */
  private List<NewHouseSalesBuildingInfoDto> parseBuildingInfo(Document doc) {
    List<NewHouseSalesBuildingInfoDto> buildingInfos = new ArrayList<>();

    // 选择第二个表格
    Elements tables = doc.select("table.table.ta-c.table2.table-white");
    if (tables.size() >= 2) {
      Element buildingTable = tables.get(1); // 第二个表格
      Elements rows = buildingTable.select("tr");

      // 跳过表头
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td");
        if (cols.size() < 5) continue;

        String projectName = cols.get(0).text().trim();
        String buildingName = cols.get(1).text().trim();
        String engineeringPlanningPermit = cols.get(2).text().trim();
        String engineeringConstructionPermit = cols.get(3).text().trim();

        // 套房信息链接
        Element linkElement = cols.get(4).selectFirst("a");
        String detailLink = "";
        if (linkElement != null) {
          detailLink = linkElement.absUrl("href").trim();
        }

        NewHouseSalesBuildingInfoDto buildingInfo =
            NewHouseSalesBuildingInfoDto.builder()
                .projectName(projectName)
                .buildingName(buildingName)
                .engineeringPlanningPermit(engineeringPlanningPermit)
                .engineeringConstructionPermit(engineeringConstructionPermit)
                .detailLink(detailLink)
                .build();

        buildingInfos.add(buildingInfo);
      }
    }

    return buildingInfos;
  }

  /**
   * 解析日期字符串为LocalDate。
   *
   * @param dateStr 日期字符串，例如 "2024-08-15"
   * @return LocalDate 对象
   */
  private LocalDate parseDate(String dateStr) {
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    return LocalDate.parse(dateStr, formatter);
  }

  /**
   * 解析字符串为double。
   *
   * @param doubleStr 字符串，例如 "45095.21"
   * @return double 值
   */
  private double parseDouble(String doubleStr) throws NumberFormatException {
    return doubleStr.isEmpty() ? 0.0 : Double.parseDouble(doubleStr.replaceAll(",", "").trim());
  }

  /**
   * 解析字符串为int。
   *
   * @param intStr 字符串，例如 "600"
   * @return int 值
   */
  private int parseInt(String intStr) throws NumberFormatException {
    return intStr.isEmpty() ? 0 : Integer.parseInt(intStr.replaceAll(",", "").trim());
  }
}
