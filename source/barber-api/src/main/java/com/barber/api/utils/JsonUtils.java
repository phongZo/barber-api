package com.barber.api.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class JsonUtils {

  private JsonUtils() {
  }

  public static ObjectMapper getMapper() {
    ObjectMapper objectMapper = new ObjectMapper();
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
    objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    objectMapper.enable(SerializationFeature.INDENT_OUTPUT);

    return objectMapper;
  }

  public static String convertJsonToString(Object objectValue){
    try {
      return getMapper().writeValueAsString(objectValue);
    } catch (JsonProcessingException e) {
      log.error("===> Cannot convert object to JSON String: {}", e.getMessage());
      return null;
    }
  }

  public static <T> List<T> convertJsonStringToList(Object json, Class<T> elementClass) {
    try {
      if (json == null) {
        return null;
      }
      JavaType type = getMapper()
          .getTypeFactory()
          .constructCollectionType(List.class, elementClass);

      if (json instanceof String){
        String jsonRaw = ((String) json).trim();
        return getMapper().readValue(jsonRaw, type);
      }
      return getMapper().convertValue(json, type);
    } catch (Exception e) {
      log.error("===> Error convert JSON string to list: {}", e.getMessage());
      return null;
    }
  }
}
