package io.sitoolkit.rdg.core.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

@Slf4j
public class CsvUtils {

  private static final CSVFormat DEFAULT_FORMAT = CSVFormat.DEFAULT.withFirstRecordAsHeader();

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

  public static List<String> selectCols(List<CSVRecord> records, List<String> cols) {
    List<String> result = new ArrayList<>();

    for (CSVRecord record : records) {
      cols.stream().map(col -> record.get(col)).collect(Collectors.toList());
    }

    return result;
  }
}
