package io.sitoolkit.rdg.core.infrastructure;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;

import org.junit.Test;

public class SmartCsvWriterTest {

  @Test
  public void test() throws IOException {
    Path rootOutDir = Path.of("target/scwout");

    if (rootOutDir.toFile().exists()) {
      Files.walk(rootOutDir)
          .sorted(Comparator.reverseOrder())
          .map(Path::toFile)
          .forEach(File::delete);
    }

    List<Path> outDirPaths = List.of(rootOutDir.resolve("out1"), rootOutDir.resolve("out2"));

    String outFileName = "scwOut.csv";
    int totalRows = 10000;

    try (SmartCsvWriter writer = SmartCsvWriter.build(outDirPaths, outFileName)) {

      for (int i = 0; i < totalRows; i++) {
        writer.writeAppend(List.of(i));
      }

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    int actualTotalRows =
        outDirPaths
            .stream()
            .mapToInt(
                outDirPath -> {
                  try {
                    return Files.readAllLines(outDirPath.resolve(outFileName)).size();
                  } catch (IOException e) {
                    throw new UncheckedIOException(e);
                  }
                })
            .sum();

    assertThat(actualTotalRows, is(totalRows));
  }
}
