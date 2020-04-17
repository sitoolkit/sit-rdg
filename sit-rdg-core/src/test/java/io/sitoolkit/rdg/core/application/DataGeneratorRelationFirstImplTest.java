package io.sitoolkit.rdg.core.application;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.domain.check.CheckResult;
import io.sitoolkit.rdg.core.infrastructure.TestResourceUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class DataGeneratorRelationFirstImplTest {

  DataGeneratorRelationFirstImpl dataGenerator = new DataGeneratorRelationFirstImpl();

  DataRelationChecker checker = new DataRelationChecker();

  @Test
  public void test() throws IOException {
    Path rootPath = Path.of("target", getClass().getSimpleName());

    FileUtils.deleteDirectory(rootPath.toFile());

    Path inDir = rootPath.resolve("in");
    Path outDir = rootPath.resolve("out");

    FileUtils.deleteDirectory(inDir.toFile());
    TestResourceUtils.copy(this, "schema.json", inDir);

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
