package com.app.tuantuan.model.entity.newhouse;

import com.app.tuantuan.enumeration.HouseStatus;
import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModelProperty;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_new_house_unit_info", autoResultMap = true)
public class NewHouseUnitInfoDO extends BaseDO {

  @TableId(type = IdType.ASSIGN_UUID)
  private String id;

  private String blockId;

  private HouseStatus status;

  private String blockNumber;

  private int floorNumber;

  private String unitSourceNumber;

  private String unitNumber;

  private String usage;

  private Boolean accessibility;

  private double constructionPrice;

  private double grossFloorPrice;

  private double preSaleConstructionArea;

  private double preSaleGrossFloorArea;

  private double preSaleSharedArea;

  private double preCompletionConstructionArea;

  private double preCompletionGrossFloorArea;

  private double preCompletionSharedArea;
}
