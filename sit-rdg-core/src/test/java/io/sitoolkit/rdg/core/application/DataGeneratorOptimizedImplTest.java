package io.sitoolkit.rdg.core.application;

import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.domain.check.CheckResult;
import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfigReader;
import io.sitoolkit.rdg.core.infrastructure.E2eTestHelper;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import org.hamcrest.Matchers;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class DataGeneratorOptimizedImplTest {
  DataGeneratorOptimizedImpl generator = new DataGeneratorOptimizedImpl();

  GeneratorConfigReader reader = new GeneratorConfigReader();

  @Rule public TestName testName = new TestName();

  @Test
  public void testRelation() {
    CheckResult result = E2eTestHelper.doTest(this, testName.getMethodName(), generator);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        Matchers.is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), Matchers.is(Collections.emptyList()));
  }

  @Test
  public void testGeneration() throws IOException {
    E2eTestHelper.doTest(this, testName.getMethodName(), generator);
  }

  @Test
  public void testMultiplicity() throws IOException {
    E2eTestHelper.doTest(this, testName.getMethodName(), generator);
  }
}
