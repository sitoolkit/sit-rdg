package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.infrastructure.DataGeneratorTestHelper;
import java.io.IOException;
import org.junit.Test;

public class DataGeneratorOptimizedImplTest {

  @Test
  public void test() throws IOException {
    DataGeneratorTestHelper.doTest(this, new DataGeneratorOptimizedImpl());
  }
}
