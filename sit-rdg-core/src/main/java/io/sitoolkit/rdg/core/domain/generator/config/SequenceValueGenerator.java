package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import lombok.Data;

@Data
public class SequenceValueGenerator implements ValueGenerator {

  @JsonProperty(required = false)
  private long start = 0;

  @JsonProperty(required = false)
  private long end = Long.MAX_VALUE;

  @JsonProperty(required = false)
  private long step = 1;

  @JsonIgnore private long current;

  @Override
  public String generate(ColumnDef column) {
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
