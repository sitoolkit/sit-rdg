package io.sitoolkit.rdg.core.domain.value;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.sitoolkit.rdg.core.domain.generator.RowData;

@JsonIgnoreProperties("type")
public interface ValueGenerator {

  String generate(RowData rowData);

  void initialize();
}
