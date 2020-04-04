package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import org.apache.commons.lang3.RandomUtils;

@Data
public class RangeValueGenerator implements ValueGenerator {

  private List<RangeSpec> ranges = new ArrayList<>();

  @Override
  public String generate(ColumnDef column) {
    List<Integer> values = RatioUtils.get(ranges).getValues();
    int index = RandomUtils.nextInt(0, values.size());

    return values.get(index).toString();
  }

  @Override
  public void initialize() {
    RatioUtils.normalize(ranges);

    for (RangeSpec range : ranges) {
      for (int i = range.getMin(); i <= range.getMax(); i += range.getStep()) {
        range.getValues().add(i);
      }
    }
  }

  @Data
  static class RangeSpec implements NormalizableRatio {
    private int min;
    private int max;

    @JsonProperty(required = false)
    private int step = 1;

    private double ratio;

    @JsonIgnore private List<Integer> values = new ArrayList<>();
  }
}
