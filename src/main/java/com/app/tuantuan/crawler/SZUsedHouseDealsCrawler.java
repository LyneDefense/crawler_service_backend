package com.app.tuantuan.crawler;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsDetailDto;
import com.app.tuantuan.model.dto.housedeal.used.SZUsedHouseDealsInfoDto;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SZUsedHouseDealsCrawler extends AbstractHouseCrawler<SZUsedHouseDealsInfoDto> {

  private static final DateTimeFormatter DATE_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy年MM月dd日");
  private static final String BASE_URL = "https://zjj.sz.gov.cn/ris/szfdc/showcjgs/esfcjgs.aspx";

  public SZUsedHouseDealsCrawler() {
    super(BASE_URL);
  }

  @Override
  public List<SZUsedHouseDealsInfoDto> crawl() {
    List<SZUsedHouseDealsInfoDto> allDealsInfo = new ArrayList<>();

    try {
      // 1. 获取初始页面
      String initialPage = executeGetRequest(baseUrl);
      Document initialDoc = Jsoup.parse(initialPage);

      // 2. 提取隐藏字段
      Map<String, String> hiddenFields = extractHiddenFields(initialDoc, "input[type=hidden]");

      for (SZDistrictEnum district : SZDistrictEnum.withoutUnknownDistricts()) {
        Map<String, String> postFields = buildPostFields(hiddenFields, district);
        if (postFields == null) {
          log.warn("[未知区域: {}，跳过爬取]", district.getName());
          continue;
        }
        log.info("[开始爬取深圳二手房成交信息,区域:{}]", district.getValue());
        String responsePage = executePostRequest(baseUrl, postFields);
        Document responseDoc = Jsoup.parse(responsePage);
        SZUsedHouseDealsInfoDto dealsInfo = parseDealsInfo(responseDoc, district);
        if (dealsInfo != null) {
          allDealsInfo.add(dealsInfo);
        }

        // 更新隐藏字段以便进行下一个请求
        hiddenFields = extractHiddenFields(responseDoc, "input[type=hidden]");
      }
    } catch (IOException e) {
      throw new CustomException("爬取深圳二手房成交信息失败");
    }

    return allDealsInfo;
  }

  /**
   * 解析页面中的二手房成交信息
   *
   * @param doc Jsoup 解析后的 HTML 文档
   * @param district 当前处理的区域枚举
   * @return 包含抓取数据的 SZUsedHouseDealsInfoDto 对象
   */
  private SZUsedHouseDealsInfoDto parseDealsInfo(Document doc, SZDistrictEnum district) {
    // 获取当前日期
    Element dateElement = doc.selectFirst("span#lblCurTime1, span#lblCurTime2");
    String dateText = dateElement != null ? dateElement.text() : "未知日期";
    LocalDate date = parseDate(dateText);

    // 解析表格中的数据
    List<SZUsedHouseDealsDetailDto> details = new ArrayList<>();
    Elements tables = doc.select("table.table.ta-c.bor-b-1.table-white");
    for (Element table : tables) {
      Elements rows = table.select("tbody > tr");

      // 跳过表头
      if (rows.size() <= 1) continue;

      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td");

        if (cols.size() < 3) continue;

        String category = cols.get(0).text().trim();
        Double salesArea = parseDouble(cols.get(1).text());
        Integer salesCount = parseInteger(cols.get(2).text());

        SZUsedHouseDealsDetailDto detail =
            SZUsedHouseDealsDetailDto.builder()
                .category(category)
                .salesArea(salesArea)
                .salesCount(salesCount)
                .build();

        details.add(detail);
      }
    }
    return SZUsedHouseDealsInfoDto.builder().date(date).district(district).details(details).build();
  }

  /**
   * 解析日期字符串为 LocalDate 对象
   *
   * @param dateText 日期字符串，如 "2024年10月30日"
   * @return LocalDate 对象
   */
  private LocalDate parseDate(String dateText) {
    try {
      return LocalDate.parse(dateText, DATE_FORMATTER);
    } catch (Exception e) {
      throw new CustomException("解析日期失败");
    }
  }
}
