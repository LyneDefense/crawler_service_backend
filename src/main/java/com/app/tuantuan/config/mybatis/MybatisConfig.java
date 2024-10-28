package com.app.tuantuan.config.mybatis;

import com.app.tuantuan.config.mybatis.handler.DefaultDBFieldHandler;
import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.app.tuantuan.mapper")
public class MybatisConfig {
  @Bean
  public MybatisPlusInterceptor mybatisPlusInterceptor() {
    MybatisPlusInterceptor mybatisPlusInterceptor = new MybatisPlusInterceptor();
    mybatisPlusInterceptor.addInnerInterceptor(new PaginationInnerInterceptor()); // 分页插件
    return mybatisPlusInterceptor;
  }

  @Bean
  public MetaObjectHandler defaultMetaObjectHandler() {
    // 自动填充参数类
    return new DefaultDBFieldHandler();
  }
}
