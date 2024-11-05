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
@TableName(value = "public.sz_new_house_building_info", autoResultMap = true)
public class NewHouseBuildingInfoDO extends BaseDO {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String projectId;

    private String buildingName;
}
