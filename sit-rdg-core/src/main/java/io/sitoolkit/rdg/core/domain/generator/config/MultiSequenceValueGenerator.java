package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.util.ArrayList;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
public class MultiSequenceValueGenerator implements ValueGenerator {

  @JsonIgnore private int sequence = 1;

  private List<MultiplicitySpec> multiplicities = new ArrayList<>();

  private String subColumn;

  @JsonIgnore private SubColumnValueGenerator subColGen = new SubColumnValueGenerator();

  @JsonIgnore private List<Long> requiredCounts = new ArrayList<>();

  @JsonIgnore private long currentRequiredCount = 1;

  @JsonIgnore private int generatedCount = 0;

  @JsonProperty(required = false)
  private long totalRequiredCount = 0;

  @Override
  public String generate(ColumnDef column) {
    String value = Integer.toString(sequence);

    if (!subColGen.needsMultipleValue()) {
      sequence++;
      subColGen.reset();
    }

    if (generatedCount <= currentRequiredCount) {
      generatedCount++;
    } else {
      currentRequiredCount = requiredCounts.remove(0);
      generatedCount = 0;
    }

    log.trace(
        "Generate value: {}, sequence :{}, currentRequiredCount: {}",
        value,
        sequence,
        currentRequiredCount);

    return value;
  }

  @Override
  public void initialize() {
    // NOP
  }

  public void postDeserialize() {
    RatioUtils.normalize(multiplicities);

    for (MultiplicitySpec spec : multiplicities) {
      long requiredCount = Math.round(spec.getRatio() * totalRequiredCount);
      requiredCounts.add(requiredCount);
      subColGen.requiredMultiplicities.add(spec.getMultiplicity());
    }

    currentRequiredCount = requiredCounts.remove(0);
    subColGen.initialize();
  }

  @Data
  static class MultiplicitySpec implements NormalizableRatio {
    private double ratio = 0;
    private int multiplicity = 1;
  }

  @Slf4j
  static class SubColumnValueGenerator implements ValueGenerator {

    private int sequence = 1;

    private long currentMultiplicity = 1;

    private List<Integer> requiredMultiplicities = new ArrayList<>();

    private int index = 0;

    @Override
    public String generate(ColumnDef column) {
      return Integer.toString(sequence++);
    }

    boolean needsMultipleValue() {
      return sequence <= currentMultiplicity;
    }

    void reset() {
      index = index >= requiredMultiplicities.size() - 1 ? 0 : index + 1;
      sequence = 1;
      currentMultiplicity = requiredMultiplicities.get(index);
    }

    @Override
    public void initialize() {
      currentMultiplicity = requiredMultiplicities.get(0);
    }
  }
}
