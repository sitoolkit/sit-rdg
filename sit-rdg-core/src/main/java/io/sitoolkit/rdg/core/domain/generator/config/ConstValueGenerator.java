package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import lombok.Data;

@Data
class ConstValueGenerator implements ValueGenerator {

  private String value;

  @Override
  public String generate(RowData rowData) {
    return value;
  }
}
