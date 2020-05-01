package io.sitoolkit.rdg.core.domain.value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.generator.config.ColumnConfig;

@JsonIgnoreProperties("type")
public interface ValueGenerator {

  String generate(RowData rowData);

  default void initialize() {
    // NOP
  }

  default void initialize(ColumnConfig config) {
    // NOP
  }
}
