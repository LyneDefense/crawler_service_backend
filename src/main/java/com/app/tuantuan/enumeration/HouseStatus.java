package com.app.tuantuan.enumeration;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum HouseStatus {
  UNKNOWN("UNKNOWN", "未知状态"),
  PENDING_SALE("PENDING_SALE", "期房待售"),
  SIGNED_SUBSCRIPTION("SIGNED_SUBSCRIPTION", "已签认购书"),
  CONTRACT_ENTERED("CONTRACT_ENTERED", "已录入合同"),
  CONTRACT_SIGNED("CONTRACT_SIGNED", "已签合同"),
  RECORDED("RECORDED", "已备案"),
  FIRST_REGISTRATION("FIRST_REGISTRATION", "首次登记"),
  AFFORDABLE_HOUSING("AFFORDABLE_HOUSING", "安居房"),
  AUTOMATIC_LOCK("AUTOMATIC_LOCK", "自动锁定"),
  DISTRICT_LOCK("DISTRICT_LOCK", "区局锁定"),
  CITY_LOCK("CITY_LOCK", "市局锁定"),
  JUDICIAL_SEIZURE("JUDICIAL_SEIZURE", "司法查封");

  private final String name;
  @JsonValue private final String value;

  HouseStatus(String name, String value) {
    this.name = name;
    this.value = value;
  }

  public static HouseStatus fromName(String name) {
    for (HouseStatus type : values()) {
      if (type.getName().equals(name)) {
        return type;
      }
    }
    return UNKNOWN;
  }

  @JsonCreator
  public static HouseStatus fromValue(String value) {
    for (HouseStatus type : values()) {
      if (type.getValue().equals(value)) {
        return type;
      }
    }
    return UNKNOWN;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
