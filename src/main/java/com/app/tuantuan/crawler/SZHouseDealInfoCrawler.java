package com.app.tuantuan.crawler;

import cn.hutool.core.util.StrUtil;
import com.app.tuantuan.constant.CrawlerConstants;
import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.dto.housedeal.SZHouseDealsAreaDetailDto;
import com.app.tuantuan.model.dto.housedeal.SZHouseDealsDetailDto;
import com.app.tuantuan.model.dto.housedeal.SZHouseDealsInfoDto;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

/** <a href="https://zjj.sz.gov.cn:8004/">...</a> - 一手放成交信息-上月网签信息 */
@Slf4j
@Component
public class SZHouseDealInfoCrawler extends AbstractHouseCrawler<SZHouseDealsInfoDto> {

  public SZHouseDealInfoCrawler() {
    super(CrawlerConstants.SZ_HOUSE_DEAL_URL);
  }

  /**
   * 启动爬取过程，返回包含所有区域房产交易数据的列表。
   *
   * @return 包含所有区域房产交易数据的列表。
   * @throws IOException 如果在HTTP请求过程中发生I/O错误。
   */
  public List<SZHouseDealsInfoDto> crawl() throws IOException {
    List<SZHouseDealsInfoDto> houseDeals = new ArrayList<>();

    // 获取首页内容
    String initialPage = this.executeGetRequest(super.baseUrl);
    Document initialDoc = Jsoup.parse(initialPage);
    Map<String, String> hiddenFields =
        this.extractHiddenFields(initialDoc, CrawlerConstants.HIDDEN_INPUT_CSS_QUERY_CSS);
    String summaryText = parseSummaryText(initialDoc);
    List<String> summaryMessages = new ArrayList<>();
    if (summaryText != null) {
      summaryMessages.add(summaryText);
    }

    // 遍历所有区域进行数据爬取
    for (SZDistrictEnum district : SZDistrictEnum.withoutUnknownDistricts()) {
      log.info("[开始爬取: {},展示数据]", district.getValue());
      // 构建POST请求所需的字段
      Map<String, String> postFields = buildPostFields(hiddenFields, district);
      if (postFields == null) {
        log.warn("[未知区域: {}，跳过爬取]", district.getName());
        continue;
      }

      // 执行POST请求
      String postResponse = this.executePostRequest(this.baseUrl, postFields);
      Document postDoc = Jsoup.parse(postResponse);

      // 更新隐藏字段
      hiddenFields = this.extractHiddenFields(postDoc, CrawlerConstants.HIDDEN_INPUT_CSS_QUERY_CSS);

      // 解析日期和区域信息
      YearMonth date = this.parseDate(postDoc);
      SZDistrictEnum parsedDistrict = this.parseDistrict(postDoc);

      // 解析交易和面积统计表格
      List<SZHouseDealsDetailDto> transactions = this.parseTransactionTable(postDoc);
      List<SZHouseDealsAreaDetailDto> areas = this.parseAreaStatisticsTable(postDoc);

      // 创建并添加CityNewHouseDeals实例
      SZHouseDealsInfoDto szHouseDealsInfoDto =
          this.createCityNewHouseDeals(
              parsedDistrict,
              transactions,
              areas,
              summaryMessages,
              date.getYear(),
              date.getMonth().getValue());
      houseDeals.add(szHouseDealsInfoDto);
    }
    log.info("[数据爬取完成，总共处理区域数: {}]", houseDeals.size());
    return houseDeals;
  }

  /**
   * 解析HTML文档中的日期信息，并返回YearMonth对象。
   *
   * @param doc Jsoup解析后的HTML文档。
   * @return 解析后的YearMonth对象，如果解析失败则返回当前时间。
   */
  private YearMonth parseDate(Document doc) {
    Element dateElement = doc.selectFirst("span[id^=ctl03_lblCurTime5]");
    String dateText = dateElement != null ? dateElement.text() : "未知";
    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy年MM月");

    try {
      return YearMonth.parse(dateText, formatter);
    } catch (DateTimeParseException e) {
      log.error("[日期解析失败，原始日期: {}]", dateText);
      throw new CustomException(StrUtil.format("日期解析失败，原始日期: {}", dateText));
    }
  }

  /**
   * 从HTML文档中解析区域信息，并返回对应的区域枚举。
   *
   * @param doc Jsoup解析后的HTML文档。
   * @return 解析后的区域枚举，如果解析失败则返回UNKNOWN。
   */
  private SZDistrictEnum parseDistrict(Document doc) {
    Element districtElement = doc.selectFirst("span[id^=ctl03_lbldistrict4]");
    String districtText = districtElement != null ? districtElement.text() : "未知";
    return SZDistrictEnum.fromValue(districtText);
  }

  private String parseSummaryText(Document doc) {
    Element summaryElement = doc.selectFirst("span#ctl03_lblMonthStatistic");
    return summaryElement != null ? summaryElement.text() : null;
  }

  /**
   * 解析交易信息表格并返回交易详情列表。
   *
   * @param doc Jsoup解析后的HTML文档。
   * @return 交易详情的列表。
   */
  private List<SZHouseDealsDetailDto> parseTransactionTable(Document doc) {
    Element transactionTable = doc.selectFirst("div.fix > table.table");
    if (transactionTable != null) {
      List<SZHouseDealsDetailDto> transactions = new ArrayList<>();
      Elements rows = transactionTable.select("tbody > tr");
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td > span");
        if (cols.size() >= 5) {
          String category = cols.get(0).text();
          int transactionCount = parseInteger(cols.get(1).text());
          double transactionArea = parseDouble(cols.get(2).text());
          int endMonthSaleCount = parseInteger(cols.get(3).text());
          double endMonthSaleArea = parseDouble(cols.get(4).text());

          SZHouseDealsDetailDto transaction =
              new SZHouseDealsDetailDto(
                  category, transactionCount, transactionArea, endMonthSaleCount, endMonthSaleArea);
          transactions.add(transaction);
        }
      }
      return transactions;
    }
    return Collections.emptyList();
  }

  /**
   * 解析面积统计表格并返回面积详情列表。
   *
   * @param doc Jsoup解析后的HTML文档。
   * @return 面积详情的列表。
   */
  private List<SZHouseDealsAreaDetailDto> parseAreaStatisticsTable(Document doc) {
    Element areaStatisticsTable = doc.selectFirst("div.recordlistBox.fix > table.table");
    if (areaStatisticsTable != null) {
      List<SZHouseDealsAreaDetailDto> areas = new ArrayList<>();
      Elements rows = areaStatisticsTable.select("tbody > tr");
      // Skip header row
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td > span");
        if (cols.size() >= 3) {
          String areaRange = cols.get(0).text();
          int transactionCount = parseInteger(cols.get(1).text());
          double transactionArea = parseDouble(cols.get(2).text());

          SZHouseDealsAreaDetailDto area =
              new SZHouseDealsAreaDetailDto(areaRange, transactionCount, transactionArea);
          areas.add(area);
        }
      }
      return areas;
    }
    return Collections.emptyList();
  }

  /**
   * 创建CityNewHouseDeals实例并封装解析的数据。
   *
   * @param year 交易年份。
   * @param month 交易月份。
   * @param district 区域枚举。
   * @param transactions 交易详情列表。
   * @param areas 面积详情列表。
   * @param messages 摘要信息。
   * @return 封装后的CityNewHouseDeals实例。
   */
  private SZHouseDealsInfoDto createCityNewHouseDeals(
      SZDistrictEnum district,
      List<SZHouseDealsDetailDto> transactions,
      List<SZHouseDealsAreaDetailDto> areas,
      List<String> messages,
      int year,
      int month) {
    SZHouseDealsInfoDto szHouseDealsInfoDto = new SZHouseDealsInfoDto();
    // TODO 这里目前都是用的深圳
    szHouseDealsInfoDto.setYear(year);
    szHouseDealsInfoDto.setMonth(month);
    szHouseDealsInfoDto.setDistrict(district);
    szHouseDealsInfoDto.setDealsDetails(transactions);
    szHouseDealsInfoDto.setDealsAreaDetails(areas);
    szHouseDealsInfoDto.setMessages(messages);
    szHouseDealsInfoDto.setUpdateDate(LocalDate.now());
    return szHouseDealsInfoDto;
  }
}
