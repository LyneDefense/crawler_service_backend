package com.app.tuantuan.enumeration;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.AllArgsConstructor;
import lombok.Getter;

/** 深圳各区的枚举类 */
@Getter
@AllArgsConstructor
public enum SZDistrictEnum {
  UNKNOWN("UNKNOWN", "未知"),
  ALL("ALL", "全市"),
  BAO_AN("BAO_AN", "宝安"),
  LONG_GANG("LONG_GANG", "龙岗"),
  FU_TIAN("FU_TIAN", "福田"),
  YAN_TIAN("YAN_TIAN", "盐田"),
  LUO_HU("LUO_HU", "罗湖"),
  LONG_HUA("LONG_HUA", "龙华"),
  GUANG_MING("GUANG_MING", "光明"),
  PING_SHAN("PING_SHAN", "坪山"),
  DA_PENG("DA_PENG", "大鹏新"),
  NAN_SHAN("NAN_SHAN", "南山"),
  SHEN_SHAN("SHEN_SHAN", "深汕");

  private static final Map<String, SZDistrictEnum> VALUE_MAP =
      Stream.of(values()).collect(Collectors.toMap(SZDistrictEnum::getValue, e -> e));

  private final String name;

  @JsonValue @EnumValue private final String value;

  @JsonCreator
  public static SZDistrictEnum fromValue(String value) {
    return VALUE_MAP.getOrDefault(value, UNKNOWN);
  }

  public static List<SZDistrictEnum> withoutUnknownDistricts() {
    return Stream.of(values()).filter(e -> e != UNKNOWN).collect(Collectors.toList());
  }
}
