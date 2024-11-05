package com.app.tuantuan.crawler.newhouse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UnitNumberConvertor {
  /**
   * 解析并转换房屋单元号。
   *
   * @param unitSourceNumber 房号源编号，例如 "901", "0901", "1205", "204a", "90a1"
   * @param floorNumber 楼层号，例如 9, 2
   * @return 转换后的单元号，例如 "01", "01", "05", "04a", "90a1"
   */
  public static String convertUnitNumber(String unitSourceNumber, int floorNumber) {
    if (unitSourceNumber == null || unitSourceNumber.isEmpty()) {
      return unitSourceNumber;
    }

    // 判断是否是纯数字
    if (isNumeric(unitSourceNumber)) {
      return processNumericUnitSourceNumber(unitSourceNumber);
    } else {
      // 判断非数字是否在最后面
      if (hasNonDigitsAtEnd(unitSourceNumber)) {
        // 分离数字部分和非数字部分
        String[] parts = splitNumericAndNonNumeric(unitSourceNumber);
        String numericPart = parts[0];
        String nonNumericPart = parts[1];

        // 处理数字部分
        String processedNumericPart = processNumericUnitSourceNumber(numericPart);

        // 拼接并返回
        return processedNumericPart + nonNumericPart;
      } else {
        // 非数字不在最后面，直接返回原始单元号
        return unitSourceNumber;
      }
    }
  }

  /**
   * 判断字符串是否由纯数字组成。
   *
   * @param str 要判断的字符串
   * @return 如果是纯数字返回 true，否则返回 false
   */
  private static boolean isNumeric(String str) {
    return str.matches("^\\d+$");
  }

  /**
   * 判断非数字字符是否位于字符串的末尾。
   *
   * @param str 要判断的字符串
   * @return 如果非数字字符位于末尾返回 true，否则返回 false
   */
  private static boolean hasNonDigitsAtEnd(String str) {
    return str.matches("^\\d+\\D+$");
  }

  /**
   * 分离字符串中的数字部分和非数字部分。
   *
   * @param str 要分离的字符串
   * @return 一个包含两部分的数组，第一部分是数字，第二部分是非数字
   */
  private static String[] splitNumericAndNonNumeric(String str) {
    Pattern pattern = Pattern.compile("^(\\d+)(\\D+)$");
    Matcher matcher = pattern.matcher(str);
    if (matcher.find()) {
      return new String[] {matcher.group(1), matcher.group(2)};
    }
    return new String[] {str, ""};
  }

  /**
   * 处理纯数字的房号源编号，根据规则去除楼层号部分。
   *
   * @param numericUnitSourceNumber 纯数字的房号源编号
   * @return 处理后的单元号
   */
  private static String processNumericUnitSourceNumber(String numericUnitSourceNumber) {
    int length = numericUnitSourceNumber.length();

    if (length == 3) {
      // 情况一：3位数，去掉第一位（楼层号）
      return numericUnitSourceNumber.substring(1);
    } else if (length == 4) {
      if (numericUnitSourceNumber.startsWith("0")) {
        // 情况二：4位数，第一位是0，去掉0和接下来的楼层号
        return numericUnitSourceNumber.substring(2);
      } else {
        // 情况三：4位数，第一位不是0，去掉前两位（楼层号）
        return numericUnitSourceNumber.substring(2);
      }
    } else {
      // 其他情况，直接返回原始数字部分
      return numericUnitSourceNumber;
    }
  }
}
