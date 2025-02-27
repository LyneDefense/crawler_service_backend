package com.app.tuantuan.model.entity.housedeal.used;

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
@TableName(value = "public.sz_used_house_deals_detail", autoResultMap = true)
public class SZUsedHouseDealsDetailDO extends BaseDO {

  @TableId(type = IdType.ASSIGN_UUID)
  private String id;

  private String parentId;

  private String category;

  private Integer salesCount;

  private Double salesArea;
}
