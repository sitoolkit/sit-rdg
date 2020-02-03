package io.sitoolkit.rdg.core.infrastructure;

import java.io.IOException;
import java.nio.file.Path;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JsonUtils {

  private static ObjectMapper mapper;

  static {
    mapper = new ObjectMapper();
    mapper.setSerializationInclusion(NON_NULL);
    mapper.setSerializationInclusion(NON_EMPTY);
  }

  public static <T> String object2json(T source) {
    try {
      return mapper.writeValueAsString(source);
    } catch (JsonProcessingException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> T json2object(Path json, Class<T> type) {
    try {
      return mapper.readValue(json.toFile(), type);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
