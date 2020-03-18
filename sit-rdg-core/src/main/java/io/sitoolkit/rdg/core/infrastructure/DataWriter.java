package io.sitoolkit.rdg.core.infrastructure;

import java.io.Closeable;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

public interface DataWriter extends Closeable {

  void writeAppend(List<Object> line) throws IOException;

  List<Path> getFiles();

  static DataWriter build(List<Path> outDirs, String fileName) throws IOException {

    if (outDirs.size() == 1) {
      return new CsvWriter(outDirs.get(0).resolve(fileName));
    } else {
      return SmartCsvWriter.build(outDirs, fileName);
    }
  }
}
