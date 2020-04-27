package io.sitoolkit.rdg.core.domain.value;

import io.sitoolkit.rdg.core.domain.generator.RandomValueUtils;
import io.sitoolkit.rdg.core.domain.generator.RowData;

public class VarcharGenerator extends DataTypeValueGenerator {

  @Override
  public String generate(RowData rowData) {
    return RandomValueUtils.generateRandomAlphabetic(1, getDataType().getSize());
  }
}
