package io.sitoolkit.rdg.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.application.DataRelationChecker;
import io.sitoolkit.rdg.core.application.DataRelationChecker.CheckResult;
import io.sitoolkit.rdg.core.infrastructure.TestResourceUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Test;

public class ComplexRelationTest {

  Main main = new Main();

  DataRelationChecker checker = new DataRelationChecker();

  @Test
  public void testMultiFkWithChild() {
    Path inDir = Path.of("target/main/in");
    Path outDir = Path.of("target/main/out");

    TestResourceUtils.copy(this, "mult-fk-with-child.sql", inDir);

    int exitCode =
        main.execute(
            new String[] {"read-sql", "gen-data", "-i", inDir.toString(), "-o", outDir.toString()});

    assertThat("exit code is 0", exitCode, is(0));

    CheckResult result = checker.checkDir(inDir, outDir);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }

  @Test
  public void testSelfJoinWithParent() throws IOException {
    Path rootDir = Path.of("target/self-join-parent");

    FileUtils.deleteDirectory(rootDir.toFile());

    Path inDir = rootDir.resolve("in");
    Path outDir = rootDir.resolve("out");

    TestResourceUtils.copy(this, "self-join-with-parent.sql", inDir);

    int exitCode =
        main.execute(
            new String[] {"read-sql", "gen-data", "-i", inDir.toString(), "-o", outDir.toString()});

    assertThat("exit code is 0", exitCode, is(0));

    CheckResult result = checker.checkDir(inDir, outDir);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }

  @Test
  public void testSelfJoinWithChild() throws IOException {
    Path rootDir = Path.of("target/self-join-child");

    FileUtils.deleteDirectory(rootDir.toFile());

    Path inDir = rootDir.resolve("in");
    Path outDir = rootDir.resolve("out");

    TestResourceUtils.copy(this, "self-join-with-child.sql", inDir);

    int exitCode =
        main.execute(
            new String[] {"read-sql", "gen-data", "-i", inDir.toString(), "-o", outDir.toString()});

    assertThat("exit code is 0", exitCode, is(0));

    CheckResult result = checker.checkDir(inDir, outDir);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }
}
