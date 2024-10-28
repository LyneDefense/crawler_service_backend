package com.app.tuantuan.model.entity.onlinesign;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_contract_online_sign_area_detail", autoResultMap = true)
public class SZContractOnlineSignAreaDetailDO extends BaseDO {

  private String id;

  private String parentId;

  private String areaRange;

  private int contractOnlineSignCount;

  private double contractOnlineSignArea;
}
