package com.app.tuantuan.component;

import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.introspect.JacksonAnnotationIntrospector;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.module.afterburner.AfterburnerModule;
import java.text.SimpleDateFormat;
import java.util.TimeZone;

public class ObjectMapperFactory {

  public static ObjectMapper create() {
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    sdf.setTimeZone(TimeZone.getTimeZone("Asia/Shanghai"));
    SimpleModule simpleModule = new SimpleModule();
    return new ObjectMapper()
        .registerModules(new JavaTimeModule())
        .registerModule(new AfterburnerModule())
        .registerModule(simpleModule)
        .setAnnotationIntrospector(new JacksonAnnotationIntrospector())
        .setDateFormat(sdf)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }
}
