package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.DataType;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.RandomStringUtils;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RandomValueUtils {

  private static final DateTimeFormatter TIME_FORMATTER =
      DateTimeFormatter.ofPattern("hh:mm:ss.SSS");

  private static final DateTimeFormatter TIMESTAMP_FORMATTER =
      DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS");

  public static String generate(ColumnDef col) {
    return generate(col.getDataType());
  }

  public static String generate(DataType dataType) {

    switch (dataType.getName()) {
      case CHAR:
        return RandomStringUtils.randomAlphabetic(dataType.getSize());
      case DATE:
        return generateRandomDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
      case DECIMAL:
        return generateRandomDecimal(dataType.getIntegerDigit(), dataType.getDecimalDigit())
            .toString();
      case FLOAT:
        return Double.toString(ThreadLocalRandom.current().nextDouble(100));
      case INTEGER:
        return Integer.toString(ThreadLocalRandom.current().nextInt(100));
      case TIME:
        return generateRandomTime().format(TIME_FORMATTER);
      case TIMESTAMP:
        return generateRandomDateTime().format(TIMESTAMP_FORMATTER);
      case VARCHAR:
        return generateRandomAlphabetic(1, dataType.getSize());
      default:
        return RandomStringUtils.randomAlphabetic(1);
    }
  }

  public static String generateRandomAlphabetic(int rangeFrom, int rangeTo) {
    int range = ThreadLocalRandom.current().nextInt(rangeFrom, rangeTo + 1);
    return RandomStringUtils.randomAlphabetic(range);
  }

  public static BigDecimal generateRandomDecimal(int integerDigit, int decimalDigit) {
    double bound = Math.pow(10, integerDigit);
    double dval = ThreadLocalRandom.current().nextDouble(bound);
    BigDecimal value = BigDecimal.valueOf(dval);
    return value.setScale(decimalDigit, RoundingMode.DOWN);
  }

  public static LocalDate generateRandomDate() {
    return LocalDate.now().minusDays(ThreadLocalRandom.current().nextInt(100));
  }

  public static LocalTime generateRandomTime() {
    return LocalTime.now().minusSeconds(ThreadLocalRandom.current().nextInt(100));
  }

  public static LocalDateTime generateRandomDateTime() {
    return LocalDateTime.now().minusSeconds(ThreadLocalRandom.current().nextInt(10000));
  }

  public static String generateRandomDateStr() {
    return generateRandomDate().format(DateTimeFormatter.ISO_LOCAL_DATE);
  }

  public static String generateRandomTimestampStr() {
    return generateRandomDateTime().format(TIMESTAMP_FORMATTER);
  }
}
