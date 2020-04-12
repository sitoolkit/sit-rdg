package io.sitoolkit.rdg.core.application;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.infrastructure.CsvData;
import io.sitoolkit.rdg.core.infrastructure.CsvUtils;
import io.sitoolkit.rdg.core.infrastructure.TestResourceUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DataGeneratorRelationFirstImplTest {

  DataGeneratorRelationFirstImpl dataGenerator = new DataGeneratorRelationFirstImpl();

  @Test
  public void test() throws IOException {
    Path inDir = Path.of("target/gen-in");
    FileUtils.deleteDirectory(inDir.toFile());
    TestResourceUtils.copy(this, "schema.json", inDir);

    Path outDir = Path.of("target/gen-out");
    FileUtils.deleteDirectory(outDir.toFile());

    List<Path> outFiles = dataGenerator.generate(inDir, List.of(outDir));

    CsvData tab_1_data = CsvUtils.read(outFiles.get(0));
    CsvData tab_2_data = CsvUtils.read(outFiles.get(1));
    CsvData tab_3_data = CsvUtils.read(outFiles.get(2));

    assertThat(tab_1_data.getFileName(), is("tab_1.csv"));
    assertThat(tab_2_data.getFileName(), is("tab_2.csv"));
    assertThat(tab_3_data.getFileName(), is("tab_3.csv"));

    assertThat(
        "tab_1(col_1_1) contains all tab_2(col_2_1)",
        tab_1_data.containsAll("col_1_1", tab_2_data, "col_2_1"),
        is(true));

    assertThat(
        "tab_2(col_2_1, col_2_2) contains all tab_3(col_3_1, col_3_2)",
        tab_2_data.containsAll(
            List.of("col_2_1", "col_2_2"), tab_3_data, List.of("col_3_1", "col_3_2")),
        is(true));
  }
}
