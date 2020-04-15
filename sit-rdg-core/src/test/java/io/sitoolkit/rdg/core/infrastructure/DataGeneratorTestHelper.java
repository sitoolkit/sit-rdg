package io.sitoolkit.rdg.core.infrastructure;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.application.DataGenerator;
import io.sitoolkit.rdg.core.application.DataRelationChecker;
import io.sitoolkit.rdg.core.application.DataRelationChecker.CheckResult;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class DataGeneratorTestHelper {

  static DataRelationChecker checker = new DataRelationChecker();

  public static void doTest(Object test, DataGenerator dataGenerator) throws IOException {
    Path rootPath = Path.of("target", test.getClass().getSimpleName());

    FileUtils.deleteDirectory(rootPath.toFile());

    Path inDir = rootPath.resolve("in");
    Path outDir = rootPath.resolve("out");

    FileUtils.deleteDirectory(inDir.toFile());
    TestResourceUtils.copy(test, "schema.json", inDir);

    FileUtils.deleteDirectory(outDir.toFile());

    List<Path> outFiles = dataGenerator.generate(inDir, List.of(outDir));

    CheckResult result = checker.checkFiles(inDir, outFiles);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }
}
