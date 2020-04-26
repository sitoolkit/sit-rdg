package io.sitoolkit.rdg.core.infrastructure;

import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_EMPTY;
import static com.fasterxml.jackson.annotation.JsonInclude.Include.NON_NULL;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
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
      throw new UncheckedIOException(e);
    }
  }

  public static Path object2jsonFile(Object source, Path filePath) {
    log.info("Write: {}", filePath.toAbsolutePath().normalize());
    try {
      Files.writeString(filePath, object2json(source));
      return filePath;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T> T json2object(Path json, Class<T> type) {
    log.info("Read: {}", json.toAbsolutePath().normalize());
    try {
      return mapper.readValue(json.toFile(), type);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
