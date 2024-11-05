package com.app.tuantuan.crawler;

import com.app.tuantuan.constant.CustomException;
import com.app.tuantuan.enumeration.SZDistrictEnum;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.cookie.BasicCookieStore;
import org.apache.hc.client5.http.cookie.CookieStore;
import org.apache.hc.client5.http.entity.UrlEncodedFormEntity;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ClassicHttpResponse;
import org.apache.hc.core5.http.NameValuePair;
import org.apache.hc.core5.http.message.BasicNameValuePair;
import org.apache.hc.core5.util.Timeout;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public abstract class AbstractHouseCrawler<T> {

  protected final CloseableHttpClient httpClient;
  protected final String baseUrl;

  public AbstractHouseCrawler(String baseUrl) {
    CookieStore cookieStore = new BasicCookieStore();
    RequestConfig requestConfig =
        RequestConfig.custom()
            .setConnectTimeout(Timeout.ofMinutes(5))
            .setResponseTimeout(Timeout.ofMinutes(5))
            .build();
    this.httpClient =
        HttpClients.custom()
            .setDefaultCookieStore(cookieStore)
            .setDefaultRequestConfig(requestConfig)
            .build();
    this.baseUrl = baseUrl;
  }

  /**
   * 抽象方法，用于爬取数据。
   *
   * @return 包含爬取数据的列表。
   * @throws IOException 如果在爬取过程中发生I/O错误。
   */
  public abstract List<T> crawl() throws IOException;

  /**
   * 执行HTTP GET请求并返回响应内容。
   *
   * @param url 要发送GET请求的URL。
   * @return 响应内容的字符串表示。
   * @throws IOException 如果在请求过程中发生I/O错误。
   */
  protected String executeGetRequest(String url) throws IOException {
    HttpGet get = new HttpGet(url);
    return httpClient.execute(get, this::handleResponse);
  }

  /**
   * 执行HTTP POST请求并返回响应内容。
   *
   * @param url 要发送POST请求的URL。
   * @param postFields 包含POST请求参数的键值对映射。
   * @return 响应内容的字符串表示。
   * @throws IOException 如果在请求过程中发生I/O错误。
   */
  protected String executePostRequest(String url, Map<String, String> postFields)
      throws IOException {
    HttpPost post = new HttpPost(url);
    List<NameValuePair> urlParameters = new ArrayList<>();
    postFields.forEach((key, value) -> urlParameters.add(new BasicNameValuePair(key, value)));
    post.setEntity(new UrlEncodedFormEntity(urlParameters));
    return httpClient.execute(post, this::handleResponse);
  }

  /**
   * 处理HTTP响应，检查状态码并返回响应内容。
   *
   * @param response HTTP响应对象。
   * @return 响应内容的字符串表示。
   * @throws IOException 如果响应状态码不在200-299范围内。
   */
  protected String handleResponse(ClassicHttpResponse response) throws IOException {
    int status = response.getCode();
    if (status >= 200 && status < 300) {
      return new String(response.getEntity().getContent().readAllBytes(), StandardCharsets.UTF_8);
    } else {
      throw new IOException("爬取数据Http请求状态异常，状态码:" + status);
    }
  }

  /**
   * 从HTML文档中提取所有隐藏的输入字段。
   *
   * @param doc Jsoup解析后的HTML文档。
   * @param cssQuery cssQuery
   * @return 包含隐藏字段名称和值的映射。
   */
  protected Map<String, String> extractHiddenFields(Document doc, String cssQuery) {
    Map<String, String> hiddenFields = new HashMap<>();
    Elements inputs = doc.select(cssQuery);
    for (Element input : inputs) {
      hiddenFields.put(input.attr("name"), input.attr("value"));
    }
    return hiddenFields;
  }

  /**
   * 根据指定区域构建POST请求所需的字段。
   *
   * @param hiddenFields 从页面中提取的隐藏字段。
   * @param district 当前处理的区域枚举。
   * @return 包含所有POST请求参数的映射，如果区域未知则返回null。
   */
  protected Map<String, String> buildPostFields(
      Map<String, String> hiddenFields, SZDistrictEnum district) {
    String eventTarget = getEventTargetByDistrict(district);
    if (eventTarget == null) {
      return null;
    }
    Map<String, String> postFields = new HashMap<>(hiddenFields);
    postFields.put("__EVENTTARGET", eventTarget);
    postFields.put("__EVENTARGUMENT", "");
    // 添加其他必要的字段，如果需要的话
    return postFields;
  }

  /**
   * 根据区域枚举返回对应的__EVENTTARGET值。
   *
   * @param district 区域枚举。
   * @return 对应的__EVENTTARGET值，如果区域未知则返回null。
   */
  protected static String getEventTargetByDistrict(SZDistrictEnum district) {
    return switch (district) {
      case ALL -> "hypAll";
      case BAO_AN -> "hypBa";
      case FU_TIAN -> "hypFt";
      case LONG_GANG -> "hypLg";
      case LUO_HU -> "hypLh";
      case NAN_SHAN -> "hypNs";
      case YAN_TIAN -> "hypYt";
      case LONG_HUA -> "hypLongHua";
      case PING_SHAN -> "hypPs";
      case GUANG_MING -> "hypGm";
      case DA_PENG -> "hypDP";
      case SHEN_SHAN -> "hypsshz";
      default -> null;
    };
  }

  /**
   * 将字符串解析为整数，处理可能的格式异常。
   *
   * @param text 要解析的字符串。
   * @return 解析后的整数，如果解析失败则返回0。
   */
  protected int parseInteger(String text) {
    try {
      return Integer.parseInt(text.replaceAll(",", "").trim());
    } catch (NumberFormatException e) {
      throw new CustomException("字符串解析为整数失败");
    }
  }

  /**
   * 将字符串解析为双精度浮点数，处理可能的格式异常。
   *
   * @param text 要解析的字符串。
   * @return 解析后的双精度浮点数，如果解析失败则返回0.0。
   */
  protected double parseDouble(String text) {
    try {
      return Double.parseDouble(text.replaceAll(",", "").trim());
    } catch (NumberFormatException e) {
      throw new CustomException("字符串解析为浮点数失败");
    }
  }

  /**
   * 关闭HTTP客户端，释放相关资源。
   *
   * @throws IOException 如果在关闭过程中发生I/O错误。
   */
  protected void close() throws IOException {
    httpClient.close();
  }
}
