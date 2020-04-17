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

  private DataTypeName name = DataTypeName.CHAR;

  private int size = 5;

  private int integerDigit;

  private int decimalDigit;

  private String unit;
}
