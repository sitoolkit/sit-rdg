package io.sitoolkit.rdg.core.domain.value;

import io.sitoolkit.rdg.core.domain.generator.RandomValueUtils;
import io.sitoolkit.rdg.core.domain.generator.RowData;

public class TimestampGenerator extends DataTypeValueGenerator {

  @Override
  public String generate(RowData rowData) {
    return RandomValueUtils.generateRandomTimestampStr();
  }
}
