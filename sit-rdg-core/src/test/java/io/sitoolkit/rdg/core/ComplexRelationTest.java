package io.sitoolkit.rdg.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.application.DataRelationChecker;
import io.sitoolkit.rdg.core.application.DataRelationChecker.CheckResult;
import io.sitoolkit.rdg.core.infrastructure.TestResourceUtils;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.junit.Test;

public class ComplexRelationTest {

  Main main = new Main();

  DataRelationChecker checker = new DataRelationChecker();

  @Test
  public void test() {
    Path inDir = Path.of("target/main/in");
    Path outDir = Path.of("target/main/out");

    TestResourceUtils.copy(this, "complex-test.sql", inDir);

    int exitCode =
        main.execute(
            new String[] {"read-sql", "gen-data", "-i", inDir.toString(), "-o", outDir.toString()});

    assertThat("exit code is 0", exitCode, is(0));

    CheckResult result = checker.check(inDir, outDir);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }
}
