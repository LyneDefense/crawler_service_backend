package com.app.tuantuan.service.caller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.*;
import javax.annotation.PostConstruct;


public abstract class BaseHttpServiceCaller {

  final ObjectMapper objectMapper =
          new ObjectMapper()
                  .setSerializationInclusion(JsonInclude.Include.ALWAYS)
                  .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
                  .configure(SerializationFeature.INDENT_OUTPUT, true)
                  .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

  @PostConstruct
  abstract void init();
}
