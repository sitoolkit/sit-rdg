package io.sitoolkit.rdg.core.infrastructure;

import java.io.Closeable;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

public class CsvWriter implements Closeable {

  private CSVPrinter printer;

  public CsvWriter(Path out, CSVFormat format) throws IOException {
    this.printer = new CSVPrinter(new FileWriter(out.toString()), format);
  }

  public CsvWriter(Path out) throws IOException {
    this(out, CSVFormat.DEFAULT);
  }

  public void writeAppend(List<Object> line) throws IOException {
    printer.printRecord(line);
    printer.flush();
  }

  @Override
  public void close() throws IOException {
    printer.close();
  }
}
