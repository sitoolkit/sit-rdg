package io.sitoolkit.rdg.core.domain.schema;

import org.junit.Test;

import static org.junit.Assert.*;

public class DataTypeTest {

  @Test
  public void parse() {
    DataType type = DataType.parse("double precision");
    assertEquals(DataType.DOUBLE_PRECISION, type);
  }
}