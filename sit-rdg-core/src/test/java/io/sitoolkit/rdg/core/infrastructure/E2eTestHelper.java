package io.sitoolkit.rdg.core.infrastructure;

import io.sitoolkit.rdg.core.application.DataGenerator;
import io.sitoolkit.rdg.core.application.DataRelationChecker;
import io.sitoolkit.rdg.core.domain.check.CheckResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import org.apache.commons.io.FileUtils;

public class E2eTestHelper {

  static DataRelationChecker checker = new DataRelationChecker();

  public static CheckResult doTest(Object testClass, String testName, DataGenerator dataGenerator) {
    Path rootPath = Path.of("target", testClass.getClass().getSimpleName(), testName);

    try {
      FileUtils.deleteDirectory(rootPath.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    Path inDir = rootPath.resolve("in");
    Path outDir = rootPath.resolve("out");

    TestResourceUtils.copy(testClass, testName, "schema.json", inDir);
    TestResourceUtils.copy(testClass, testName, "generator-config.json", inDir);

    List<Path> outFiles = dataGenerator.generate(inDir, List.of(outDir));

    return checker.checkFiles(inDir, outFiles);
  }
}
