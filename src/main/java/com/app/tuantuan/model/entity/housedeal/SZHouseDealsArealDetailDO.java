package com.app.tuantuan.model.entity.housedeal;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_house_deals_area_detail", autoResultMap = true)
public class SZHouseDealsArealDetailDO extends BaseDO {

  @TableId(type = IdType.ASSIGN_UUID)
  private String id;

  // HouseDealsInfoEntity的关联id
  private String parentId;

  private String areaRange;

  private int dealCount;

  private double dealArea;
}
