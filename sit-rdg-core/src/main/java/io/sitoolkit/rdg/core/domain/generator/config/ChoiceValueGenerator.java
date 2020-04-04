package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class ChoiceValueGenerator implements ValueGenerator {

  private List<Choice> values = new ArrayList<>();

  @Override
  public String generate(ColumnDef column) {
    return RatioUtils.get(values).getValue();
  }

  @Override
  public void initialize() {
    RatioUtils.normalize(values);
  }

  @Data
  public static class Choice implements NormalizableRatio {
    private String value;
    private double ratio;
  }
}
