package com.app.tuantuan.enumeration;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

@Getter
public enum DateFormat {
  YEAR("YEAR"),
  MONTH("MONTH"),
  DAY("DAY"),
  ;

  @JsonValue
  private final String name;

  DateFormat(String name) {
    this.name = name;
  }
}
