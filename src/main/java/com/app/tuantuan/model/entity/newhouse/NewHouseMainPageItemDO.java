package com.app.tuantuan.model.entity.newhouse;


import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import java.time.LocalDate;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "public.sz_new_house_main_page_item", autoResultMap = true)
public class NewHouseMainPageItemDO extends BaseDO {

    private String id;

    private String preSaleNumber;

    private String preSaleNumberLink;

    private String projectName;

    private String projectNameLink;

    private String developer;

    private String district;

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate approvalDate;
}
