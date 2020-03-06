package io.sitoolkit.rdg.core.domain.schema;

import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

public enum DataType {
  CHARACTER,
  CHAR,
  CHARACTER_VARYING,
  TEXT,
  VARCHAR,
  VARCHAR2,
  SMALLINT,
  INTEGER,
  BIGINT,
  DECIMAL,
  NUMERIC,
  REAL,
  DOUBLE_PRECISION,
  NUMBER,
  TIME,
  DATE,
  TIMESTAMP,
  MEANS_DATE,
  MEANS_ID,
  MEANS_DECIMAL,
  UNKNOWN;

  public static DataType parse(Object object) {
    return Stream.of(values())
        .filter(v -> StringUtils.equals(
            v.name(), object.toString().toUpperCase().replace(" ", "_")))
        .findFirst()
        .orElse(UNKNOWN);
  }
}
