package com.app.tuantuan.config.mybatis.handler;

import com.app.tuantuan.model.entity.BaseDO;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import java.time.LocalDateTime;
import java.util.Objects;
import org.apache.ibatis.reflection.MetaObject;

/**
 * 通用参数填充实现类
 *
 * <p>如果没有显式的对通用参数进行赋值，这里会对通用参数进行填充、赋值
 */
public class DefaultDBFieldHandler implements MetaObjectHandler {

  @Override
  public void insertFill(MetaObject metaObject) {
    if (Objects.nonNull(metaObject) && metaObject.getOriginalObject() instanceof BaseDO BaseDO) {

      LocalDateTime now = LocalDateTime.now();
      // 创建时间为空，则以当前时间为插入时间
      if (Objects.isNull(BaseDO.getCreateTime())) {
        BaseDO.setCreateTime(now);
      }
      // 更新时间为空，则以当前时间为更新时间
      if (Objects.isNull(BaseDO.getUpdateTime())) {
        BaseDO.setUpdateTime(now);
      }
    }
  }

  @Override
  public void updateFill(MetaObject metaObject) {
    // 更新时间为空，则以当前时间为更新时间
    Object modifyTime = getFieldValByName("updateTime", metaObject);
    if (Objects.isNull(modifyTime)) {
      setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
    }
  }
}
