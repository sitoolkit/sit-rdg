package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import java.util.Objects;
import lombok.Getter;

public class SequentialCharValueGenerator extends SequenceValueGenerator
    implements AlignmentCharValueGenerator {

  @Getter Alignment align;

  @Override
  public String generate(RowData rowData) {
    String val = super.generate(rowData);

    if (Objects.isNull(align)) {
      return val;
    } else {
      return padding(val);
    }
  }
}
