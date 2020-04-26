package io.sitoolkit.rdg.core;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.domain.check.CheckResult;
import io.sitoolkit.rdg.core.infrastructure.E2eTestHelper;
import java.util.Collections;
import java.util.List;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class MainTest {

  @Rule public TestName testName = new TestName();

  Main main = new Main();

  @Test
  public void testMultiFkWithChild() {
    CheckResult result = E2eTestHelper.doTest(this, testName.getMethodName(), main);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }

  @Test
  public void testSelfJoinWithParent() {
    CheckResult result = E2eTestHelper.doTest(this, testName.getMethodName(), main);

    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }

  @Test
  public void testSelfJoinWithChild() {
    CheckResult result = E2eTestHelper.doTest(this, testName.getMethodName(), main);
    assertThat(
        "genereated files and order",
        result.getChekedFileNames(),
        is(List.of("tab_1.csv", "tab_2.csv", "tab_3.csv")));

    assertThat("relation check error", result.getErrorList(), is(Collections.emptyList()));
  }
}
