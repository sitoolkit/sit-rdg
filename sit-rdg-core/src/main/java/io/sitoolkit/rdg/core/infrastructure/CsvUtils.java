package io.sitoolkit.rdg.core.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.lang.reflect.InvocationTargetException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class CsvUtils {

  private static final CSVFormat DEFAULT_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader();

  public static boolean isCsvFile(Path file) {
    return file.toString().endsWith(".csv");
  }

  public static CsvData read(Path file) {

    try {
      log.info("Read:\n\n{}\n{}", file, Files.readString(file));

      return new CsvData(
          CSVParser.parse(file, Charset.defaultCharset(), DEFAULT_FORMAT).getRecords(),
          file.getFileName().toString());

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static <T extends Collection<List<String>>> T selectCols(
      List<CSVRecord> records, List<String> cols, Class<T> type) {

    try {
      T result = type.getDeclaredConstructor().newInstance();
      for (CSVRecord record : records) {
        result.add(cols.stream().map(record::get).collect(Collectors.toList()));
      }

      return result;
    } catch (InstantiationException
        | IllegalAccessException
        | IllegalArgumentException
        | InvocationTargetException
        | NoSuchMethodException
        | SecurityException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
