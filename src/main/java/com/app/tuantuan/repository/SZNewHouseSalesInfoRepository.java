package com.app.tuantuan.repository;

import com.app.tuantuan.config.mybatis.query.LambdaQueryWrapperX;
import com.app.tuantuan.mapper.NewHouseSalesInfoMapper;
import com.app.tuantuan.model.dto.newhouse.NewHouseMainPageItemDto;
import com.app.tuantuan.model.dto.newhouse.NewHouseSalesInfoDto;
import com.app.tuantuan.model.entity.newhouse.NewHouseSalesInfoDO;
import java.util.List;
import javax.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class SZNewHouseSalesInfoRepository {

  @Resource NewHouseSalesInfoMapper newHouseSalesInfoMapper;

  public List<NewHouseSalesInfoDto> selectNewHouseSalesInfos(String projectId) {
    return newHouseSalesInfoMapper
        .selectList(
            new LambdaQueryWrapperX<NewHouseSalesInfoDO>()
                .eq(NewHouseSalesInfoDO::getProjectId, projectId))
        .stream()
        .map(NewHouseSalesInfoDto::of)
        .toList();
  }

  public void deleteNewHouseSalesInfos(String projectId) {
    List<NewHouseSalesInfoDO> entities =
        newHouseSalesInfoMapper.selectList(
            new LambdaQueryWrapperX<NewHouseSalesInfoDO>()
                .eq(NewHouseSalesInfoDO::getProjectId, projectId));
    if (entities.isEmpty()) {
      log.info("[删除-未找到'房源项目卖方信息',projectId为: {} 的卖方信息,跳过删除]", projectId);
      return;
    }
    int i =
        newHouseSalesInfoMapper.deleteBatchIds(
            entities.stream().map(NewHouseSalesInfoDO::getId).toList());
    log.info(
        "[删除'房源项目卖方信息',projectId为: {} 的卖方信息成功,找到:{}条记录,共删除:{} 条记录]", projectId, entities.size(), i);
  }

  public void deleteBatchNewHouseSalesInfos(List<String> projectIds) {
    int i =
        newHouseSalesInfoMapper.delete(
            new LambdaQueryWrapperX<NewHouseSalesInfoDO>()
                .inIfPresent(NewHouseSalesInfoDO::getProjectId, projectIds));
    log.info("[批量删除'房源项目卖方信息'成功,找到:{}条记录,共删除:{} 条记录]", projectIds.size(), i);
  }

  public void saveNewHouseSalesInfos(
      NewHouseSalesInfoDto salesInfo, NewHouseMainPageItemDto maiPageItem) {
    log.info("[保存房源项目'卖方信息',项目名称:{}]", maiPageItem.getProjectName());
    NewHouseSalesInfoDO entity = salesInfo.to(null, maiPageItem.getId());
    newHouseSalesInfoMapper.insert(entity);
  }
}
