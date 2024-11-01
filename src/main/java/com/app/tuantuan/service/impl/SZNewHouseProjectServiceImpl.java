package com.app.tuantuan.service.impl;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.crawler.newhouse.NewHouseMainPageCrawler;
import com.app.tuantuan.mapper.NewHouseMainPageItemMapper;
import com.app.tuantuan.model.base.PageResult;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageReqDto;
import com.app.tuantuan.model.entity.newhouse.NewHouseMainPageItemDO;
import com.app.tuantuan.service.ISZNewHouseProjectService;
import java.time.LocalDate;
import java.util.List;
import javax.annotation.Resource;
import javax.validation.constraints.NotNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class SZNewHouseProjectServiceImpl implements ISZNewHouseProjectService {

  @Resource NewHouseMainPageItemMapper newHouseMainPageItemMapper;
  @Resource NewHouseMainPageCrawler newHouseMainPageCrawler;

  @Override
  public PageResult<NewHouseMainPageItemDto> selectNewHouseMainPageItem(
      NewHouseMainPageReqDto reqDto) {
    LocalDate startDate = LocalDate.of(2021, 1, 1);
    LocalDate endDate = LocalDate.now();
    if (reqDto.getStartDate().isAfter(startDate)) {
      startDate = reqDto.getStartDate();
    }
    if (reqDto.getEndDate().isBefore(endDate)) {
      endDate = reqDto.getEndDate();
    }
    PageResult<NewHouseMainPageItemDO> entities =
        newHouseMainPageItemMapper.selectPage(
            reqDto,
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .between(NewHouseMainPageItemDO::getApprovalDate, startDate, endDate)
                .orderByDesc(NewHouseMainPageItemDO::getApprovalDate));
    return new PageResult<>(
        entities.getList().stream().map(NewHouseMainPageItemDto::of).toList(), entities.getTotal());
  }

  @Override
  @Transactional
  public void crawlAndSaveMainPageItems(NewHouseMainPageReqDto reqDto) {
    List<NewHouseMainPageItemDto> dtos = newHouseMainPageCrawler.crawl(reqDto);
    List<NewHouseMainPageItemDO> entities = dtos.stream().map(NewHouseMainPageItemDto::to).toList();
    this.deleteMaiPageItems(reqDto.getStartDate(), reqDto.getEndDate());
    newHouseMainPageItemMapper.insertBatch(entities);
    log.info("[保存一手房源公示首页信息成功,数量:{}]", entities.size());
  }

  @Override
  public void deleteMaiPageItems(@NotNull LocalDate startDate, @NotNull LocalDate endDate) {
    List<NewHouseMainPageItemDO> entities =
        newHouseMainPageItemMapper.selectList(
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .between(NewHouseMainPageItemDO::getApprovalDate, startDate, endDate));
    if (entities.isEmpty()) {
      log.info("[未找到{}-{}一手房源公示首页信息,跳过删除操作]", startDate, endDate);
      return;
    }
    int i =
        newHouseMainPageItemMapper.delete(
            new LambdaQueryWrapperX<NewHouseMainPageItemDO>()
                .between(NewHouseMainPageItemDO::getApprovalDate, startDate, endDate));
    log.info(
        "[删除一手房源公示首页信息成功,时间:{} - {},查询到:{}记录，删除:{}条记录]", startDate, endDate, entities.size(), i);
  }
}
