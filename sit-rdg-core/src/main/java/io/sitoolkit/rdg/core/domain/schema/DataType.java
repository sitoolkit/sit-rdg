package io.sitoolkit.rdg.core.domain.schema;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public enum DataType {
  CHAR,
  VARCHAR,
  VARCHAR2,
  NUMBER,
  DATE,
  TIMESTAMP,
  MEANS_DATE,
  MEANS_ID,
  MEANS_DECIMAL,
  UNKNOWN;

  public static DataType parse(Object object) {
    return Stream.of(values())
        .filter(v -> StringUtils.equals(v.name(), object.toString().toUpperCase()))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
