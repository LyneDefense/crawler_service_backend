package com.app.tuantuan.model.entity.onlinesign.onsale;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_onsale_subscription_online_sign_detail", autoResultMap = true)
public class SZOnsaleContractOnlineSignDetailDO extends BaseDO {

  private String id;

  private String parentId;

  @ApiModelProperty("用途")
  private String category;

  @ApiModelProperty("网签面积")
  private Double salesArea;

  @ApiModelProperty("网签套数")
  private Integer salesCount;
}
