package io.sitoolkit.rdg.core.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;

public class SqlFileUtils {

  public static boolean isSqlFile(Path filePath) {
    return StringUtils.endsWith(filePath.getFileName().toString(), ".sql");
  }

  public static String readSql(Path path) {

    try {
      String sql =
          Files.readAllLines(path, StandardCharsets.UTF_8).stream()
              .filter(text -> SqlIgnoreLinePattern.chainAllPattern().test(text.toLowerCase()))
              .collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
              .toString();

      return camel2snake(sql);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static final String camel2snake(String camel) {

    if (StringUtils.isEmpty(camel)) {
      return camel;
    }

    camel = formatSql(camel);

    StringBuilder sb = new StringBuilder(camel.length() + camel.length());

    for (int i = 0; i < camel.length(); i++) {

      Character c = camel.charAt(i);
      Character backC = 0 <= i - 1 ? camel.charAt(i - 1) : Character.valueOf(' ');
      Character nextC = i + 1 < camel.length() ? camel.charAt(i + 1) : Character.valueOf(' ');

      if (Character.isUpperCase(c)
          && !StringUtils.equals(c.toString(), " ")
          && !StringUtils.equals(c.toString(), "(")
          && !StringUtils.equals(c.toString(), ")")
          && !StringUtils.equals(backC.toString(), " ")
          && !StringUtils.equals(backC.toString(), ".")
          && !StringUtils.equals(backC.toString(), ",")
          && !StringUtils.equals(backC.toString(), "(")
          && !StringUtils.equals(backC.toString(), ")")
          && !StringUtils.equals(nextC.toString(), "_")
          && !StringUtils.equals(nextC.toString(), " ")
          && !StringUtils.equals(nextC.toString(), ".")
          && !StringUtils.equals(nextC.toString(), ",")
          && !StringUtils.equals(nextC.toString(), "(")
          && !StringUtils.equals(nextC.toString(), ")")
          && !Character.isUpperCase(nextC)
          && !nextC.toString().matches("[0-9]")) {

        sb.append(sb.length() != 0 ? '_' : "").append(c);

      } else {
        sb.append(c);
      }
    }
    return sb.toString().toUpperCase();
  }

  public static final String formatSql(String sql) {
    return Arrays.stream(sql.split(" "))
        .map(String::trim)
        .reduce(
            (s1, s2) -> {
              if (!StringUtils.startsWith(s2, "(")
                  && StringUtils.endsWith(s2, "\"")
                  && !StringUtils.startsWith(s2, "\"")) {
                return String.join("_", s1, s2);
              }
              return String.join(" ", s1, s2);
            })
        .orElse(sql)
        .replace("\"", "");
  }
}
