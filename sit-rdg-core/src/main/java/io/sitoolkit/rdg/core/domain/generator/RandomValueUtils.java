package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.ThreadLocalRandom;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;

@Slf4j
public class RandomValueUtils {

  public static String generate(ColumnDef col) {

    switch (col.meansDataType()) {
      case CHAR:
        {
          return RandomStringUtils.randomAlphabetic(col.getIntegerDigit());
        }
      case VARCHAR:
      case VARCHAR2:
        {
          int min = 1;
          int max = col.getIntegerDigit();
          return RandomValueUtils.createRandomAlphabetic(min, max);
        }
      case NUMBER:
        {
          return RandomValueUtils.createRandomNumeric(col.getIntegerDigit(), col.getDecimalDigit());
        }
      case DATE:
        {
          return RandomValueUtils.createRandomDate()
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        }
      case TIMESTAMP:
        {
          return RandomValueUtils.createRandomDateTime()
              .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"));
        }
      case MEANS_DATE:
        {
          if (8 == col.getIntegerDigit()) {
            return RandomValueUtils.createRandomDate()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
          }
          if (6 == col.getIntegerDigit()) {
            return RandomValueUtils.createRandomDate()
                .format(DateTimeFormatter.ofPattern("yyyyMM"));
          }
        }
      case MEANS_ID: {
        int max = col.getIntegerDigit();
        return RandomValueUtils.createRandomAlphabetic(max, max);
      }
      case MEANS_DECIMAL: {
        // TODO: 仮置き
        return RandomValueUtils.createRandomNumeric(col.getIntegerDigit(), 0);
      }
      case UNKNOWN: {
        log.info("Unknown dataType:{}", col);
      }
      default: {
        return "";
      }
    }
  }

  public static String createRandomAlphabetic(int rangeFrom, int rangeTo) {
    int range = ThreadLocalRandom.current().nextInt(rangeFrom, rangeTo + 1);
    return RandomStringUtils.randomAlphabetic(range);
  }

  public static String createRandomNumeric(Integer integerDigit, Integer decimalDigit) {
    String integer = createRandomNumeric(integerDigit, false);
    String decimal = createRandomNumeric(decimalDigit, true);
    if (decimal.isEmpty()) {
      return integer;
    }
    return String.format("%s.%s", integer, decimal);
  }

  public static String createRandomNumeric(Integer digit, boolean startsWithZero) {
    if (startsWithZero) {
      return StringUtils.stripEnd(RandomStringUtils.randomNumeric(digit), "0");
    } else {
      int first = ThreadLocalRandom.current().nextInt(8) + 1;
      String after = RandomStringUtils.randomNumeric(digit - 1);
      return first + after;
    }
  }

  public static LocalDate createRandomDate() {
    LocalDate now = LocalDate.now();
    return createRandomDate(now.minusYears(ThreadLocalRandom.current().nextInt(31)), now);
  }

  public static LocalDate createRandomDate(LocalDate from, LocalDate to) {
    long days = from.until(to, ChronoUnit.DAYS);
    long randomDays = ThreadLocalRandom.current().nextLong(days + 1);
    return from.plusDays(randomDays);
  }

  public static LocalTime createRandomTime() {
    LocalTime max = LocalTime.MAX;
    int hour = ThreadLocalRandom.current().nextInt(max.getHour() + 1);
    int minute = ThreadLocalRandom.current().nextInt(max.getMinute() + 1);
    int second = ThreadLocalRandom.current().nextInt(max.getSecond() + 1);
    int nanoOfSecond = ThreadLocalRandom.current().nextInt(max.getNano() + 1);
    return LocalTime.of(hour, minute, second, nanoOfSecond);
  }

  public static LocalDateTime createRandomDateTime() {
    return LocalDateTime.of(createRandomDate(), createRandomTime());
  }
}
