package io.sitoolkit.rdg.core.domain.value;

import io.sitoolkit.rdg.core.domain.schema.DataType;
import lombok.Getter;
import lombok.Setter;

public abstract class DataTypeValueGenerator implements ValueGenerator {

  @Getter @Setter private DataType dataType;

  @Override
  public void initialize() {
    // NOP
  }
}
