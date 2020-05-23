package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import lombok.Data;

@Data
public class MultiplicityConfig implements NormalizableRatio {

  private double ratio;

  private int multiplicity;

  private String pattern;

  @JsonIgnore private long rowCount;
}
