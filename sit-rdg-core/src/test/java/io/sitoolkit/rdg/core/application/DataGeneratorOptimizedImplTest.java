package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.infrastructure.E2eTestHelper;
import java.io.IOException;
import org.junit.Test;

public class DataGeneratorOptimizedImplTest {

  @Test
  public void test() throws IOException {
    E2eTestHelper.doTest(this, new DataGeneratorOptimizedImpl());
  }
}
