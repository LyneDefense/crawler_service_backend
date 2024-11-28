package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.mapper.NewHouseMainPageItemMapper;
import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageReqDto;
import com.app.tuantuan.model.entity.newhouse.NewHouseMainPageItemDO;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Repository
public class SZNewHouseMainPageRepository {

  @Resource NewHouseMainPageItemMapper newHouseMainPageItemMapper;

  /**
   * 根据主页面ID查询主页面项。
   *
   * @param mainPageId 主页面ID
   * @return 包含主页面项的 NewHouseMainPageItemDto 对象
   */
  public NewHouseMainPageItemDto selectMainPageItemById(String mainPageId) {
    return NewHouseMainPageItemDto.of(newHouseMainPageItemMapper.selectById(mainPageId));
  }

  /**
   * 根据预售号码查询主页面项。
   *
   * @param preSalleNumber 预售号码
   * @return 包含主页面项的 NewHouseMainPageItemDto 对象
   */
  public NewHouseMainPageItemDto selectMainPageItemByPreSaleNumber(String preSalleNumber) {
    NewHouseMainPageItemDO entity =
        newHouseMainPageItemMapper.selectOne(
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .eq(NewHouseMainPageItemDO::getPreSaleNumber, preSalleNumber));
    return NewHouseMainPageItemDto.of(entity);
  }

  /**
   * 根据主页面ID列表查询主页面项。
   *
   * @param mainPageIds 主页面ID列表
   * @return 包含主页面项的 NewHouseMainPageItemDto 列表
   */
  public List<NewHouseMainPageItemDto> selectMainPageItemByIds(List<String> mainPageIds) {
    return newHouseMainPageItemMapper
        .selectList(
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .in(NewHouseMainPageItemDO::getId, mainPageIds))
        .stream()
        .map(NewHouseMainPageItemDto::of)
        .toList();
  }

  /**
   * 查询最大的批准日期。
   *
   * @return 主页预售简练中的最大的批准日期
   */
  public LocalDate selectMaxApprovalDate() {
    NewHouseMainPageItemDO itemDO =
        newHouseMainPageItemMapper.selectOne(
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .orderByDesc(NewHouseMainPageItemDO::getApprovalDate)
                .last("LIMIT 1"));
    return itemDO != null ? itemDO.getApprovalDate() : null;
  }

  /**
   * 删除主页面项。
   *
   * @param mainPageItemDO 主页面项对象
   */
  public void deleteMainPageItem(NewHouseMainPageItemDO mainPageItemDO) {
    int i = newHouseMainPageItemMapper.deleteById(mainPageItemDO);
    if (i == 1) {
      log.info(
          "[删除已存在的预售证号为:{},项目名称为:{},的一手房源公示首页信息]",
          mainPageItemDO.getPreSaleNumber(),
          mainPageItemDO.getProjectName());
    }
  }

  private LambdaQueryWrapper<NewHouseMainPageItemDO> selectNewHouseListQuery(
      NewHouseMainPageReqDto reqDto) {
    LocalDate startDate = LocalDate.of(2020, 1, 1);
    LocalDate endDate = LocalDate.now();
    if (reqDto.getStartDate().isAfter(startDate)) {
      startDate = reqDto.getStartDate();
    }
    if (reqDto.getEndDate().isBefore(endDate)) {
      endDate = reqDto.getEndDate();
    }
    LambdaQueryWrapper<NewHouseMainPageItemDO> query =
        new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
            .between(NewHouseMainPageItemDO::getApprovalDate, startDate, endDate)
            .orderByDesc(NewHouseMainPageItemDO::getApprovalDate);
    if (reqDto.getProjectName() != null) {
      query.eq(NewHouseMainPageItemDO::getProjectName, reqDto.getProjectName());
    }
    return query;
  }

  /**
   * 根据请求参数选择新房主页面项。
   *
   * @param reqDto 请求参数对象
   * @return 包含新房主页面项的 PageResult 对象
   */
  public PageResult<NewHouseMainPageItemDO> selectNewHouseMainPageItem(
      NewHouseMainPageReqDto reqDto) {
    return newHouseMainPageItemMapper.selectPage(reqDto, selectNewHouseListQuery(reqDto));
  }

  public List<NewHouseMainPageItemDto> selectNewHouseMainPageItemList(
      NewHouseMainPageReqDto reqDto) {
    return newHouseMainPageItemMapper.selectList(selectNewHouseListQuery(reqDto)).stream()
        .map(NewHouseMainPageItemDto::of)
        .toList();
  }

  /**
   * 保存主页面项。
   *
   * @param dto 主页面项数据传输对象
   * @return 保存后的 NewHouseMainPageItemDto 对象
   */
  public NewHouseMainPageItemDto saveMainPageItem(NewHouseMainPageItemDto dto) {
    log.info("[保存预售证号为:{},项目名称为:{},的一手房源公示首页信息项]", dto.getPreSaleNumber(), dto.getProjectName());
    NewHouseMainPageItemDO entity = dto.to(null);
    newHouseMainPageItemMapper.insert(entity);
    return NewHouseMainPageItemDto.of(entity);
  }

  /**
   * 批量保存主页面项。
   *
   * @param dtos 主页面项数据传输对象列表
   */
  @Transactional
  public void saveMainPageItems(List<NewHouseMainPageItemDto> dtos) {
    if (dtos.isEmpty()) {
      return;
    }
    // 提取预售号码
    List<String> preSaleNumbers =
        dtos.stream().map(NewHouseMainPageItemDto::getPreSaleNumber).toList();
    // 查询数据库中存在的预售号码
    List<NewHouseMainPageItemDO> existingItems =
        newHouseMainPageItemMapper.selectList(
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .in(NewHouseMainPageItemDO::getPreSaleNumber, preSaleNumbers));
    // 提取需要删除的和新的预售号码
    List<String> existingPreSaleNumbers =
        existingItems.stream().map(NewHouseMainPageItemDO::getPreSaleNumber).toList();
    List<String> newPreSaleNumbers =
        preSaleNumbers.stream().filter(number -> !existingPreSaleNumbers.contains(number)).toList();
    log.info("需要删除的预售证号: {}", existingPreSaleNumbers);
    log.info("本次更新的预售证号: {}", newPreSaleNumbers);
    // 删除存在的记录
    newHouseMainPageItemMapper.delete(
        new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
            .inIfPresent(NewHouseMainPageItemDO::getPreSaleNumber, existingPreSaleNumbers));
    // 转换并保存新记录
    List<NewHouseMainPageItemDO> entities =
        dtos.stream().map(dto -> dto.to(null)).collect(Collectors.toList());
    newHouseMainPageItemMapper.insertBatch(entities);
  }
}
