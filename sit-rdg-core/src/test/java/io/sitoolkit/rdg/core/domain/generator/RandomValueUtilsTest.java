package io.sitoolkit.rdg.core.domain.generator;

import static org.hamcrest.text.MatchesPattern.*;
import static org.junit.Assert.*;

import io.sitoolkit.rdg.core.domain.schema.DataType;
import io.sitoolkit.rdg.core.domain.schema.DataTypeName;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@Slf4j
@RunWith(Parameterized.class)
public class RandomValueUtilsTest {

  private static final String DATE_PATTERN = "\\d{4}-\\d{2}-\\d{2}";

  private static final String TIME_PATTERN = "\\d{2}:\\d{2}:\\d{2}\\.{1}\\d{0,3}";

  @Parameter(0)
  public DataTypeName dataTypeName;

  @Parameter(1)
  public String dataTypeArgs;

  @Parameter(2)
  public String expectedPattern;

  @Parameters
  public static List<Object[]> data() {
    return List.of(
        new Object[][] {
          {DataTypeName.CHAR, "3", ".{3}"},
          {DataTypeName.DATE, "", DATE_PATTERN},
          {DataTypeName.DECIMAL, "6,4", "\\d{1,2}\\.?\\d{0,4}"},
          {DataTypeName.FLOAT, "", "\\d*\\.\\d*"},
          {DataTypeName.TIME, "", TIME_PATTERN},
          {DataTypeName.TIMESTAMP, "", DATE_PATTERN + " " + TIME_PATTERN},
          {DataTypeName.VARCHAR, "10", ".{1,10}"},
        });
  }

  @Test
  public void test() {
    DataType dataType = DataType.builder().name(dataTypeName).build();
    dataTypeName.resolve(Arrays.asList(dataTypeArgs.split(",")), dataType);

    for (int i = 0; i < 10; i++) {
      String actual = RandomValueUtils.generate(dataType);

      log.debug("Generate value {} for {}({})", actual, dataTypeName, dataTypeArgs);

      assertThat(actual, matchesPattern(expectedPattern));
    }
  }

  // @Test
  // public void charTest() {
  //   String generatedValue = generate("char", DataTypeName.CHAR, "3");
  //   assertThat(generatedValue, matchesPattern(".{3}"));
  // }

  // @Test
  // public void characterTest() {
  //   String generatedValue = generate("character", DataTypeName.CHARACTER, "3");
  //   assertThat(generatedValue, matchesPattern(".{3}"));
  // }

  // @Test
  // public void characterVaryingTest() {
  //   String generatedValue = generate("characterVarying", DataTypeName.CHARACTER_VARYING, "3");
  //   assertThat(generatedValue, matchesPattern(".{1,3}"));
  // }

  // @Test
  // public void textTest() {
  //   String generatedValue = generate("text", DataTypeName.TEXT);
  //   assertThat(generatedValue, matchesPattern(".{1,10}"));
  // }

  // @Test
  // public void smallintTest() {
  //   String generatedValue = generate("smallint", DataTypeName.SMALLINT);
  //   assertTrue(generatedValue.matches("[0-9]{3}"));
  // }

  // @Test
  // public void tinyintTest() {
  //   String generatedValue = generate("tinyint", DataTypeName.TINYINT, "3");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}"));
  // }

  // @Test
  // public void integerTest() {
  //   String generatedValue = generate("integer", DataTypeName.INTEGER, "3");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}"));
  // }

  // @Test
  // public void mediumintTest() {
  //   String generatedValue = generate("mediumint", DataTypeName.MEDIUMINT, "3");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}"));
  // }

  // @Test
  // public void bigintTest() {
  //   String generatedValue = generate("bigint", DataTypeName.BIGINT, "3");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}"));
  // }

  // @Test
  // public void decimalTest() {
  //   String generatedValue = generate("decimal", DataTypeName.DECIMAL, "5", "2");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  // }

  // @Test
  // public void numericTest() {
  //   String generatedValue = generate("numeric", DataTypeName.NUMERIC, "5", "2");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  // }

  // @Test
  // public void realTest() {
  //   String generatedValue = generate("real", DataTypeName.REAL, "5", "2");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  // }

  // @Test
  // public void floatTest() {
  //   String generatedValue = generate("float", DataTypeName.FLOAT, "5", "2");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  // }

  // @Test
  // public void doublePrecisionTest() {
  //   String generatedValue = generate("doublePrecision", DataTypeName.DOUBLE_PRECISION, "5", "2");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  // }

  // @Test
  // public void numberTest() {
  //   String generatedValue = generate("numeric", DataTypeName.NUMBER, "5", "2");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  // }

  // @Test
  // public void timeTest() {
  //   String generatedValue = generate("time", DataTypeName.TIME);
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}"));
  // }

  // @Test
  // public void dateTest() {
  //   String generatedValue = generate("date", DataTypeName.DATE);
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"));
  // }

  // @Test
  // public void timestampTest() {
  //   String generatedValue = generate("timestamp", DataTypeName.TIMESTAMP);
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(
  //       generatedValue.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}
  // [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}"));
  // }

  // @Test
  // public void unknownTest() {
  //   String generatedValue = generate("unknown", DataTypeName.parse("???"), "20", "10");
  //   log.info("generated: {}", generatedValue);
  //   assertTrue(generatedValue.isEmpty());
  // }

  // private String generate(String columnName, DataTypeName dataTypeName, String... args) {
  //   ColumnDef column = new ColumnDef();
  //   column.setName(columnName);
  //   DataType dataType = DataType.builder().name(dataTypeName).build();
  //   dataTypeName.resolve(Arrays.asList(args), dataType);
  //   column.setDataType(dataType);
  //   return RandomValueUtils.generate(column);
  // }
}
