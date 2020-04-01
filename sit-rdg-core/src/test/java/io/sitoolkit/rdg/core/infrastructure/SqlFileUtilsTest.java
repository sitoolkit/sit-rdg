package io.sitoolkit.rdg.core.infrastructure;

import org.junit.Assert;
import org.junit.Test;

public class SqlFileUtilsTest {

  @Test
  public void test() {

    String sql =
        "CREATE TABLE \"UpperCamelTable\" ("
            + "\"Column1\" VARCHAR(10), "
            + "\"Column2\" NUMBER(5, 1), "
            + "Column3 DATE, "
            + "CONSTRAINT \"UpperCamelTable\"_PKC PRIMARY KEY (\"Column1\")"
            + ")";

    String result = SqlFileUtils.camel2snake(sql);

    String expect =
        "CREATE TABLE UPPER_CAMEL_TABLE ("
            + "COLUMN1 VARCHAR(10), "
            + "COLUMN2 NUMBER(5, 1), "
            + "COLUMN3 DATE, "
            + "CONSTRAINT UPPER_CAMEL_TABLE_PKC PRIMARY KEY (COLUMN1)"
            + ")";

    Assert.assertEquals(expect, result);
  }
}
