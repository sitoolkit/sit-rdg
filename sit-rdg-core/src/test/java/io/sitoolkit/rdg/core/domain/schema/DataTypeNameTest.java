package io.sitoolkit.rdg.core.domain.schema;

import static org.junit.Assert.*;

import org.junit.Test;

public class DataTypeNameTest {

  @Test
  public void parse() {
    DataTypeName type = DataTypeName.parse("double precision");
    assertEquals(DataTypeName.FLOAT, type);
  }
}
