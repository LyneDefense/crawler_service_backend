package com.app.tuantuan.model.entity.newhouse;

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
@TableName(value = "public.sz_new_house_sales_building_info", autoResultMap = true)
public class NewHouseSalesBuildingInfoDO extends BaseDO {

  @TableId(type = IdType.ASSIGN_UUID)
  private String id;

  private String projectId;

  private String projectName;

  private String buildingName;

  private String engineeringPlanningPermit;

  private String engineeringConstructionPermit;

  private String detailLink;
}
