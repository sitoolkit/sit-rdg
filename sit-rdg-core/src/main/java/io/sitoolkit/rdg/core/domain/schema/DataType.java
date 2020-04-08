package io.sitoolkit.rdg.core.domain.schema;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Builder
@NoArgsConstructor
@Data
public class DataType {

  private DataTypeName name;

  private int size;

  private int integerDigit;

  private int decimalDigit;

  private String unit;
}
