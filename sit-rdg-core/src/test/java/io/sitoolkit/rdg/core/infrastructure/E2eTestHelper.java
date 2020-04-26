package io.sitoolkit.rdg.core.infrastructure;

import io.sitoolkit.rdg.core.Main;
import io.sitoolkit.rdg.core.application.DataGenerator;
import io.sitoolkit.rdg.core.application.DataRelationChecker;
import io.sitoolkit.rdg.core.domain.check.CheckResult;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import lombok.AllArgsConstructor;
import org.apache.commons.io.FileUtils;

public class E2eTestHelper {

  static DataRelationChecker checker = new DataRelationChecker();

  public static CheckResult doTest(Object testClass, String testName, DataGenerator dataGenerator) {

    SetUpResult result = setUp(testClass, testName);

    List<Path> outFiles = dataGenerator.generate(result.inDir, List.of(result.outDir));

    return checker.checkFiles(result.inDir, outFiles);
  }

  public static CheckResult doTest(Object testClass, String testName, Main main) {
    SetUpResult result = setUp(testClass, testName);

    main.execute(
        new String[] {
          "read-sql", "gen-data", "-i", result.inDir.toString(), "-o", result.outDir.toString()
        });

    return checker.checkDir(result.inDir, result.outDir);
  }

  public static SetUpResult setUp(Object testClass, String testName) {
    Path workDir = TestResourceUtils.workDir(testClass.getClass().getSimpleName(), testName);

    try {
      FileUtils.deleteDirectory(workDir.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    Path inDir = workDir.resolve("in");
    Path outDir = workDir.resolve("out");

    TestResourceUtils.copyResDir(testClass, testName, inDir);

    return new SetUpResult(inDir, outDir);
  }

  @AllArgsConstructor
  static class SetUpResult {
    Path inDir;
    Path outDir;
  }
}
