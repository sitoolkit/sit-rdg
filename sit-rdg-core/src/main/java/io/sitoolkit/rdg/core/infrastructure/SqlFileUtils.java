package io.sitoolkit.rdg.core.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class SqlFileUtils {

  private SqlFileUtils() {}

  public static boolean isSqlFile(Path filePath) {
    return StringUtils.endsWith(filePath.getFileName().toString(), ".sql");
  }

  public static String readSql(Path path) {

    log.info("Read: {}", path.toAbsolutePath().normalize());

    try {
      return Files.readString(path, StandardCharsets.UTF_8).replace("\"", "").toLowerCase();
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
