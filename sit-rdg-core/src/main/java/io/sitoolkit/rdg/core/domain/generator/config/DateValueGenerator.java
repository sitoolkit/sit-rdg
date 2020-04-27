package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Data;

@Data
public class DateValueGenerator implements ValueGenerator {

  private List<DateRangeSpec> ranges = new ArrayList<>();

  private String pattern;

  @JsonIgnore private DateTimeFormatter formatter;

  @Override
  public String generate(RowData rowData) {
    DateRangeSpec spec = RatioUtils.get(ranges);
    long days = ThreadLocalRandom.current().nextLong(spec.getSpan());
    return spec.getFromObj().plusDays(days).format(formatter);
  }

  @Override
  public void initialize() {
    RatioUtils.normalize(ranges);
    ranges.stream().forEach(DateRangeSpec::initialize);
    formatter = DateTimeFormatter.ofPattern(pattern);
  }

  @Data
  static class DateRangeSpec implements NormalizableRatio {
    private String from = "1900-01-01";
    private String to = "2200-01-01";
    @JsonIgnore private LocalDate fromObj;
    @JsonIgnore private long span;
    private double ratio;

    void initialize() {
      fromObj = LocalDate.parse(from);
      LocalDate toObj = LocalDate.parse(to);
      span = ChronoUnit.DAYS.between(fromObj, toObj);
    }
  }
}
