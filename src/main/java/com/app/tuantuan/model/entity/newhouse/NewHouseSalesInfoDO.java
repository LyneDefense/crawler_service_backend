package com.app.tuantuan.model.entity.newhouse;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import java.time.LocalDate;
import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_new_house_sales_info", autoResultMap = true)
public class NewHouseSalesInfoDO extends BaseDO {

    @TableId(type = IdType.ASSIGN_UUID)
    private String id;

    private String projectId;

    private String projectName;

    private String plotNumber;

    private String plotLocation;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate transferDate;

    private String district;

    private String ownershipSource;

    private String approvingAuthority;

    private String contractNumber;

    private String useYears;

    private String supplementaryAgreement;

    private String landPlanningPermit;

    private String houseUsage;

    private String landUsage;

    private String landGrade;

    private double baseArea;

    private double plotArea;

    private double totalConstructArea;

    private int presaleTotalSets;

    private double presaleTotalArea;

    private int onsaleTotalSets;

    private double onsaleTotalArea;

    private String engineeringSupervisionAgency;

    private String propertyManagementCompany;

    private String managementFee;
}
