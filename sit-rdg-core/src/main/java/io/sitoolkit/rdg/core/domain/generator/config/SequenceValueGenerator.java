package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import lombok.Data;

@Data
public class SequenceValueGenerator implements ValueGenerator {

  @JsonProperty(required = false)
  private long start = 1;

  @JsonProperty(required = false)
  private long end = Long.MAX_VALUE;

  @JsonProperty(required = false)
  private long step = 1;

  @JsonIgnore private long current;

  @Override
  public String generate(RowData rowData) {
    long next = current;

    current += step;

    if (current > end) {
      current = start;
    }

    return Long.toString(next);
  }

  @Override
  public void initialize() {
    current = start;
  }
}
