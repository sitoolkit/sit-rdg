package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import lombok.Data;

@Data
public class ConstantGenerator implements ValueGenerator {

  private String value;

  @Override
  public String generate(RowData rowData) {
    return value;
  }

  @Override
  public void initialize() {
    // NOP

  }
}
