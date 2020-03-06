package io.sitoolkit.rdg.core.domain.generator;

import java.util.List;

import org.junit.Test;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.DataType;
import lombok.extern.slf4j.Slf4j;
import static org.junit.Assert.*;

@Slf4j
public class RandomValueUtilsTest {

  @Test
  public void charTest() {
    String generatedValue = generate("char", DataType.CHAR, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches(".{3}"));
  }

  @Test
  public void characterTest() {
    String generatedValue = generate("character", DataType.CHARACTER, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches(".{3}"));
  }

  @Test
  public void characterVaryingTest() {
    String generatedValue = generate("characterVarying", DataType.CHARACTER_VARYING, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.length() <= 3);
  }

  @Test
  public void textTest() {
    String generatedValue = generate("text", DataType.TEXT, "10");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.length() <= 10);
  }

  @Test
  public void smallintTest() {
    String generatedValue = generate("smallint", DataType.SMALLINT, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}"));
  }

  @Test
  public void tinyintTest() {
    String generatedValue = generate("tinyint", DataType.TINYINT, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}"));
  }

  @Test
  public void integerTest() {
    String generatedValue = generate("integer", DataType.INTEGER, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}"));
  }

  @Test
  public void mediumintTest() {
    String generatedValue = generate("mediumint", DataType.MEDIUMINT, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}"));
  }

  @Test
  public void bigintTest() {
    String generatedValue = generate("bigint", DataType.BIGINT, "3");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}"));
  }

  @Test
  public void decimalTest() {
    String generatedValue = generate("decimal", DataType.DECIMAL, "5", "2");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  }

  @Test
  public void numericTest() {
    String generatedValue = generate("numeric", DataType.NUMERIC, "5", "2");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  }

  @Test
  public void realTest() {
    String generatedValue = generate("numeric", DataType.NUMERIC, "5", "2");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  }

  @Test
  public void floatTest() {
    String generatedValue = generate("numeric", DataType.NUMERIC, "5", "2");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  }

  @Test
  public void doublePrecisionTest() {
    String generatedValue = generate("numeric", DataType.NUMERIC, "5", "2");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  }

  @Test
  public void numberTest() {
    String generatedValue = generate("numeric", DataType.NUMERIC, "5", "2");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{3}|[0-9]{3}\\.([0-9]|[0-9]{2})"));
  }

  @Test
  public void timeTest() {
    String generatedValue = generate("time", DataType.TIME);
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}"));
  }

  @Test
  public void dateTest() {
    String generatedValue = generate("date", DataType.DATE);
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}"));
  }

  @Test
  public void timestampTest() {
    String generatedValue = generate("timestamp", DataType.TIMESTAMP);
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{4}-[0-9]{2}-[0-9]{2} [0-9]{2}:[0-9]{2}:[0-9]{2}\\.[0-9]{3}"));
  }

  @Test
  public void meansDate_yyyyMMddTest() {
    String generatedValue = generate("meansDATE", DataType.VARCHAR, "8");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{4}[0-9]{2}[0-9]{2}"));
  }

  @Test
  public void meansDate_yyyyMMTest() {
    String generatedValue = generate("meansDATE", DataType.VARCHAR, "6");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.matches("[0-9]{4}[0-9]{2}"));
  }

  @Test
  public void meansIdTest() {
    String generatedValue = generate("meansID", DataType.VARCHAR, "20");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.length() <= 20);
  }

  @Test
  public void unknownTest() {
    String generatedValue = generate("unknown", DataType.parse("???"), "20", "10");
    log.info("generated: {}", generatedValue);
    assertTrue(generatedValue.isEmpty());
  }

  private String generate(String name, DataType type, String... args) {
    ColumnDef column = new ColumnDef();
    column.setName(name);
    column.setDataType(type);
    column.setArgs(List.of(args));
    return RandomValueUtils.generate(column);
  }
}