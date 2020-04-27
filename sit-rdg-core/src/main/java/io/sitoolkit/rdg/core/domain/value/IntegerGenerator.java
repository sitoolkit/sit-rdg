package io.sitoolkit.rdg.core.domain.value;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import java.util.concurrent.ThreadLocalRandom;

public class IntegerGenerator extends DataTypeValueGenerator {

  @Override
  public String generate(RowData rowData) {
    return Integer.toString(ThreadLocalRandom.current().nextInt(100));
  }
}
