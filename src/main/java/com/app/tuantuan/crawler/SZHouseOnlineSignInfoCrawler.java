package com.app.tuantuan.crawler;

import com.app.tuantuan.constant.CrawlerConstants;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.dto.onlinesign.SZContractOnlineSignAreaDetailDto;
import com.app.tuantuan.model.dto.onlinesign.SZContractOnlineSignDetailDto;
import com.app.tuantuan.model.dto.onlinesign.SZSubscriptionOnlineSignDetailDto;
import com.app.tuantuan.model.dto.onlinesign.SZSubscriptionOnlineSignInfoDto;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SZHouseOnlineSignInfoCrawler
    extends AbstractHouseCrawler<SZSubscriptionOnlineSignInfoDto> {

  public SZHouseOnlineSignInfoCrawler() {
    super(CrawlerConstants.SZ_HOUSE_ONLINE_SIGN_URL);
  }

  @Override
  public List<SZSubscriptionOnlineSignInfoDto> crawl() throws IOException {
    List<SZSubscriptionOnlineSignInfoDto> houseSubscriptionInfos = new ArrayList<>();
    // 获取首页内容
    String initialPage = this.executeGetRequest(super.baseUrl);
    Document initialDoc = Jsoup.parse(initialPage);
    Map<String, String> hiddenFields =
        this.extractHiddenFields(initialDoc, CrawlerConstants.HIDDEN_INPUT_CSS_QUERY_CSS);
    for (SZDistrictEnum district : SZDistrictEnum.withoutUnknownDistricts()) {
      log.info("[开始爬取: {},网签信息]", district.getValue());
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
      List<SZSubscriptionOnlineSignDetailDto> subscriptionInfos = new ArrayList<>();
      List<SZContractOnlineSignDetailDto> contractInfos = new ArrayList<>();
      List<SZContractOnlineSignAreaDetailDto> contractByAreaInfos = new ArrayList<>();
      // 提取所有recordlistBox fix的div
      Elements recordListBoxes = postDoc.select("div.recordlistBox.fix");
      LocalDate currentDate = null;
      for (Element recordBox : recordListBoxes) {
        // 根据包含的span元素确定表格类型
        Element titleSpan =
            recordBox.selectFirst(
                "span[id^=ctl03_lblRGCurTime], span[id^=ctl03_lblCurTime2], span[id^=ctl03_lblCurTime5]");
        if (titleSpan != null) {
          String titleText = titleSpan.parent().text(); // 获取包含标题的文本
          if (currentDate == null) {
            currentDate = this.parseDate(titleSpan);
          }
          Element table = recordBox.selectFirst("table.ta-c.bor-b-1.table-white");
          if (table == null) {
            log.warn("未找到表格元素");
            continue;
          }
          if (titleText.contains("认购网签信息")) {
            subscriptionInfos = this.parseOnlineSignSubscriptionTable(table);
          } else if (titleText.contains("商品房购房合同网签信息")) {
            contractInfos = this.parseContractOnlineContractTable(table);
          } else if (titleText.contains("商品住房按面积统计购房合同网签信息")) {
            contractByAreaInfos = this.parseContractOnlineSignAreaTable(table);
          } else {
            log.warn("未知的表格类型: {}", titleText);
          }
        }
      }

      SZSubscriptionOnlineSignInfoDto info =
          new SZSubscriptionOnlineSignInfoDto(
              district, currentDate, subscriptionInfos, contractInfos, contractByAreaInfos);
      houseSubscriptionInfos.add(info);
    }
    return houseSubscriptionInfos;
  }

  /**
   * 根据提供的span元素解析日期。
   *
   * @param dateElement 包含日期文本的span元素。
   * @return 解析后的LocalDate对象。
   */
  public LocalDate parseDate(Element dateElement) {
    if (dateElement == null) {
      log.error("未找到日期元素");
    }
    String dateText = dateElement.text().trim();
    if (dateText.isEmpty()) {
      log.error("日期文本为空");
    }
    try {
      return LocalDate.parse(dateText, DateTimeFormatter.ofPattern("yyyy年MM月dd日"));
    } catch (DateTimeParseException e) {
      log.error("无法解析日期: {}", dateText);
    }
    return null;
  }

  private List<SZSubscriptionOnlineSignDetailDto> parseOnlineSignSubscriptionTable(Element table) {
    List<SZSubscriptionOnlineSignDetailDto> subscriptionInfos = new ArrayList<>();
    if (table != null) {
      Elements rows = table.select("tbody > tr");
      // 假设第一行是表头，跳过
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td");
        if (cols.size() >= 3) {
          String category = cols.get(0).text().trim();
          int transactionCount = this.parseInteger(cols.get(1).text());
          double transactionArea = this.parseDouble(cols.get(2).text());

          SZSubscriptionOnlineSignDetailDto info =
              new SZSubscriptionOnlineSignDetailDto(category, transactionCount, transactionArea);
          subscriptionInfos.add(info);
        }
      }
    }
    return subscriptionInfos;
  }

  private List<SZContractOnlineSignDetailDto> parseContractOnlineContractTable(Element table) {
    List<SZContractOnlineSignDetailDto> contractInfos = new ArrayList<>();
    if (table != null) {
      Elements rows = table.select("tbody > tr");
      // 假设第一行是表头，跳过
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td");
        if (cols.size() >= 5) {
          String category = cols.get(0).text().trim();
          int contractCount = this.parseInteger(cols.get(1).text());
          double contractArea = this.parseDouble(cols.get(2).text());
          int availableSalesCount = this.parseInteger(cols.get(3).text());
          double availableSalesArea = this.parseDouble(cols.get(4).text());
          SZContractOnlineSignDetailDto info =
              new SZContractOnlineSignDetailDto(
                  category, contractCount, contractArea, availableSalesCount, availableSalesArea);
          contractInfos.add(info);
        }
      }
    }
    return contractInfos;
  }

  public List<SZContractOnlineSignAreaDetailDto> parseContractOnlineSignAreaTable(Element table) {
    List<SZContractOnlineSignAreaDetailDto> contractByAreaInfos = new ArrayList<>();
    if (table != null) {
      Elements rows = table.select("tbody > tr");

      // 假设第一行是表头，跳过
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td");
        if (cols.size() >= 3) {
          String areaRange = cols.get(0).text().trim();
          int contractCount = this.parseInteger(cols.get(1).text());
          double contractArea = this.parseDouble(cols.get(2).text());

          SZContractOnlineSignAreaDetailDto info =
              new SZContractOnlineSignAreaDetailDto(areaRange, contractCount, contractArea);
          contractByAreaInfos.add(info);
        }
      }
    }

    return contractByAreaInfos;
  }
}
