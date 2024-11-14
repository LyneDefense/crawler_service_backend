package com.app.tuantuan.utils;


import com.app.tuantuan.constant.CustomException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.*;
import java.io.IOException;
import java.util.List;

public class JsonUtil {

  private static final ObjectMapper objectMapper = new ObjectMapper();

  static {
    objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE);
  }

  public static JsonNode toJsonNode(Object obj) {
    return objectMapper.valueToTree(obj);
  }

  public static JsonNode toJsonNode(String value) {
    return objectMapper.valueToTree(value);
  }

  public static <T> T fromJsonNode(JsonNode jsonNode, Class<T> tClass) {
    try {
      return objectMapper.treeToValue(jsonNode, tClass);
    } catch (JsonProcessingException e) {
      throw new CustomException("反序列化失败");
    }
  }

  public static <T> T fromJsonNode(String str, Class<T> tClass) {
    try {
      return objectMapper.readValue(str, tClass);
    } catch (JsonProcessingException e) {
      throw new CustomException("反序列化失败");
    }
  }

  public static String toJsonString(Object obj) {
    try {
      return objectMapper.writeValueAsString(obj);
    } catch (JsonProcessingException e) {
      throw new CustomException("写入json失败");
    }
  }

  public static <T> List<T> convertJsonNodeToList(
      JsonNode jsonNode, TypeReference<List<T>> typeReference) {
    try {
      return objectMapper.readValue(jsonNode.traverse(), typeReference);
    } catch (IOException e) {
      throw new CustomException("反序列化成List失败");
    }
  }
}
