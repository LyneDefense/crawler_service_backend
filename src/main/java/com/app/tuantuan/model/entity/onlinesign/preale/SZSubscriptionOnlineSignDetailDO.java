package com.app.tuantuan.model.entity.onlinesign.preale;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_subscription_online_sign_detail", autoResultMap = true)
public class SZSubscriptionOnlineSignDetailDO extends BaseDO {

  private String id;

  private String parentId;

  private String category;

  private int subscriptionCount;

  private double subscriptionArea;
}
