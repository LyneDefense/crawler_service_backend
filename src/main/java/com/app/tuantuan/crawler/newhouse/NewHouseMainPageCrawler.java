package com.app.tuantuan.crawler.newhouse;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageReqDto;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.protocol.HttpClientContext;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.jsoup.Jsoup;
import org.jsoup.nodes.*;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class NewHouseMainPageCrawler {

  private static final String BASE_URL = "https://zjj.sz.gov.cn/ris/bol/szfdc/index.aspx";

  public List<NewHouseMainPageItemDto> crawl(NewHouseMainPageReqDto params) {
    log.info(
        "[开始爬取一手房源公示首页信息,起始时间:{},终止时间,:{},项目名称:{}]",
        params.getStartDate(),
        params.getEndDate(),
        params.getProjectName());
    if (params.getStartDate().isAfter(params.getEndDate())) {
      return new ArrayList<>();
    }
    List<NewHouseMainPageItemDto> projects = new ArrayList<>();

    // 创建CookieStore以维护会话
    CookieStore cookieStore = new BasicCookieStore();
    CloseableHttpClient httpClient =
        HttpClients.custom().setDefaultCookieStore(cookieStore).build();

    HttpClientContext context = HttpClientContext.create();
    context.setCookieStore(cookieStore);

    boolean shouldContinue = true;

    try {
      // 第一次GET请求，获取初始VIEWSTATE等
      HttpGet initialGet = new HttpGet(BASE_URL);
      CloseableHttpResponse initialResponse = httpClient.execute(initialGet, context);
      String initialHtml =
          EntityUtils.toString(initialResponse.getEntity(), StandardCharsets.UTF_8);
      initialResponse.close();

      Document initialDoc = Jsoup.parse(initialHtml, BASE_URL);

      // 提取必要的隐藏字段
      Map<String, String> hiddenFields = extractHiddenFields(initialDoc);

      // 设置表单参数
      List<BasicNameValuePair> formParams =
          hiddenFields.entrySet().stream()
              .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
              .collect(Collectors.toList());

      // 设置过滤条件
      if (params.getProjectName() != null && !params.getProjectName().isEmpty()) {
        formParams.add(new BasicNameValuePair("tep_name", params.getProjectName()));
      }

      // 设置__EVENTTARGET为Button1，表示点击查询按钮
      formParams.add(new BasicNameValuePair("__EVENTTARGET", "Button1"));
      formParams.add(new BasicNameValuePair("__EVENTARGUMENT", ""));

      // 构建POST请求
      HttpPost postRequest = new HttpPost(BASE_URL);
      postRequest.setEntity(new UrlEncodedFormEntity(formParams, StandardCharsets.UTF_8));

      CloseableHttpResponse postResponse = httpClient.execute(postRequest, context);
      String postHtml = EntityUtils.toString(postResponse.getEntity(), StandardCharsets.UTF_8);
      postResponse.close();

      Document postDoc = Jsoup.parse(postHtml, BASE_URL);
      List<NewHouseMainPageItemDto> pageProjects = parseAndFilterProjectsFromDocument(postDoc);
      projects.addAll(filterByCondition(pageProjects, params));
      // 处理分页
      while (shouldContinue && hasNextPage(postDoc)) {
        // 准备下一页的POST请求
        Map<String, String> nextPageFields = extractHiddenFields(postDoc);

        List<BasicNameValuePair> nextFormParams =
            nextPageFields.entrySet().stream()
                .map(e -> new BasicNameValuePair(e.getKey(), e.getValue()))
                .collect(Collectors.toList());

        // 设置__EVENTTARGET为AspNetPager1，__EVENTARGUMENT为下一页的页码
        String nextPageArg = getNextPageArgument(postDoc);
        if (nextPageArg == null) {
          break;
        }

        nextFormParams.add(new BasicNameValuePair("__EVENTTARGET", "AspNetPager1"));
        nextFormParams.add(new BasicNameValuePair("__EVENTARGUMENT", nextPageArg));

        // 重新添加过滤条件以保持状态
        if (params.getProjectName() != null && !params.getProjectName().isEmpty()) {
          nextFormParams.add(new BasicNameValuePair("tep_name", params.getProjectName()));
        }

        // 构建下一页的POST请求
        HttpPost nextPost = new HttpPost(BASE_URL);
        nextPost.setEntity(new UrlEncodedFormEntity(nextFormParams, StandardCharsets.UTF_8));

        CloseableHttpResponse nextResponse = httpClient.execute(nextPost, context);
        String nextHtml = EntityUtils.toString(nextResponse.getEntity(), StandardCharsets.UTF_8);
        nextResponse.close();

        // 解析新页的HTML时，设置base URI以确保绝对URL正确解析
        postDoc = Jsoup.parse(nextHtml, BASE_URL);
        pageProjects = parseAndFilterProjectsFromDocument(postDoc);
        // 如果当前页的最后一个项目的 approvalDate 小于 startDate，停止爬取
        if (!pageProjects.isEmpty()) {
          NewHouseMainPageItemDto lastProject = pageProjects.get(pageProjects.size() - 1);
          // 过滤当前页面的item
          pageProjects = filterByCondition(pageProjects, params);
          if (lastProject.getApprovalDate().isBefore(params.getStartDate())) {
            shouldContinue = false;
          }
        }
        projects.addAll(pageProjects);
      }
      httpClient.close();

    } catch (Exception e) {
      throw new CustomException("解析一手房源公示首页中的项目数据失败");
    }
    log.info(
        "[爬取一手房源公示首页信息完成,起始时间:{},终止时间,:{},项目名称:{},共爬取数据条数:{}]",
        params.getStartDate(),
        params.getEndDate(),
        params.getProjectName(),
        projects.size());
    return projects;
  }

  private List<NewHouseMainPageItemDto> filterByCondition(
      List<NewHouseMainPageItemDto> pageProjects, NewHouseMainPageReqDto params) {
    return pageProjects.stream()
        .filter(
            p -> {
              boolean nameFilter =
                  params.getProjectName() != null && !params.getProjectName().isEmpty();
              boolean dateSuccess =
                  !p.getApprovalDate().isBefore(params.getStartDate())
                      && !p.getApprovalDate().isAfter(params.getEndDate());
              boolean nameSuccess =
                  !nameFilter || (p.getProjectName().contains(params.getProjectName()));
              return dateSuccess && nameSuccess;
            })
        .toList();
  }

  /** 提取页面中的所有隐藏字段 */
  private Map<String, String> extractHiddenFields(Document doc) {
    Map<String, String> hiddenFields = new HashMap<>();
    Elements inputs = doc.select("input[type=hidden]");
    for (Element input : inputs) {
      hiddenFields.put(input.attr("name"), input.attr("value"));
    }
    return hiddenFields;
  }

  /**
   * 解析页面中的项目数据，并根据条件过滤
   *
   * @param doc 页面文档
   * @return 符合条件的项目列表
   */
  private List<NewHouseMainPageItemDto> parseAndFilterProjectsFromDocument(Document doc) {
    List<NewHouseMainPageItemDto> projects = new ArrayList<>();
    Element table = doc.selectFirst("table.table.ta-c.bor-b-1.table-white");
    if (table != null) {
      Elements rows = table.select("tr");
      // 跳过表头
      for (int i = 1; i < rows.size(); i++) {
        Element row = rows.get(i);
        Elements cols = row.select("td");
        if (cols.size() < 6) continue; // 确保列数足够

        try {
          // 提取预售证号和链接
          Element preSaleLinkElement = cols.get(1).selectFirst("a");
          String preSaleNumber = preSaleLinkElement.text().trim();
          String preSaleNumberLink = preSaleLinkElement.absUrl("href").trim(); // 绝对URL

          // 提取项目名称和链接
          Element projectLinkElement = cols.get(2).selectFirst("a");
          String projectName = projectLinkElement.text().trim();
          String projectNameLink = projectLinkElement.absUrl("href").trim(); // 绝对URL

          String developer = cols.get(3).text().trim();
          String district = cols.get(4).text().trim();
          LocalDate approvalDate = LocalDate.parse(cols.get(5).text().trim());

          NewHouseMainPageItemDto project =
              new NewHouseMainPageItemDto(
                  null,
                  preSaleNumber,
                  preSaleNumberLink,
                  projectName,
                  projectNameLink,
                  developer,
                  SZDistrictEnum.fromValue(district),
                  approvalDate);
          projects.add(project);
        } catch (Exception e) {
          throw new CustomException("解析一手房源公示首页中的项目数据失败");
        }
      }
    }
    return projects;
  }

  private boolean hasNextPage(Document doc) {
    Element pager = doc.selectFirst("div#AspNetPager1");
    if (pager != null) {
      Element nextLink = pager.selectFirst("a:contains(>)");
      return nextLink != null && !nextLink.hasAttr("disabled");
    }
    return false;
  }

  /** 获取下一页的页码参数 */
  private String getNextPageArgument(Document doc) {
    Element pager = doc.selectFirst("div#AspNetPager1");
    if (pager != null) {
      Element nextLink = pager.selectFirst("a:contains(>)");
      if (nextLink != null && !nextLink.hasAttr("disabled")) {
        String href = nextLink.attr("href");
        // 使用正则表达式提取页码参数
        Pattern pattern = Pattern.compile("__doPostBack\\('AspNetPager1','(\\d+)'\\)");
        Matcher matcher = pattern.matcher(href);
        if (matcher.find()) {
          return matcher.group(1);
        }
      }
    }
    return null;
  }
}
