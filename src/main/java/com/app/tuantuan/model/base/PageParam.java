package com.app.tuantuan.model.base;

import io.swagger.annotations.ApiModelProperty;
import java.io.Serializable;
import lombok.Data;

@Data
public class PageParam implements Serializable {

  private static final Integer PAGE_NO = 1;
  private static final Integer PAGE_SIZE = 10;

  @ApiModelProperty(value = "页码，从 1 开始", required = true, example = "1")
  private Integer pageNo = PAGE_NO;

  @ApiModelProperty(value = "每页条数，最大值为 100", required = true, example = "10")
  private Integer pageSize = PAGE_SIZE;
}
