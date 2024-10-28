package com.app.tuantuan.model.entity.onlinesign;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_contract_online_sign_detail", autoResultMap = true)
public class SZContractOnlineSignDetailDO extends BaseDO {

  private String id;

  private String parentId;

  private String category;

  private int contractOnlineSignCount;

  private double contractOnlineSignArea;

  private int availableSalesCount;

  private double availableSalesArea;
}
