package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Data
@Slf4j
public class ChoiceValueGenerator implements ValueGenerator {

  private List<Choice> values = new ArrayList<>();

  @Override
  public String generate(ColumnDef column) {
    return RatioUtils.get(values).getValue();
  }

  @Override
  public void initialize() {
    RatioUtils.normalize(values);

    log.debug("Initialized: {}", values);
  }

  @Data
  public static class Choice implements NormalizableRatio {
    private String value;
    private double ratio;
  }
}
