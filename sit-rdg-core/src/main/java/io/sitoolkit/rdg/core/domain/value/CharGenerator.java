package io.sitoolkit.rdg.core.domain.value;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import org.apache.commons.lang3.RandomStringUtils;

public class CharGenerator extends DataTypeValueGenerator {

  @Override
  public String generate(RowData rowData) {
    return RandomStringUtils.randomAlphanumeric(getDataType().getSize());
  }
}
