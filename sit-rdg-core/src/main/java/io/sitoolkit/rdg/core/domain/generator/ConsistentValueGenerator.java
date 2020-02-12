package io.sitoolkit.rdg.core.domain.generator;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;

public class ConsistentValueGenerator {

  private Map<ColumnDef, ConsistentCharsGenerator> registedGenerator = new HashMap<>();

  private Map<ColumnDef, LocalDate> registedDate = new HashMap<>();

  private Map<ColumnDef, LocalDateTime> registedDateTime = new HashMap<>();

  public String generate(ColumnDef col) {
    switch (col.getDataType()) {
      case NUMBER:
      case MEANS_DECIMAL:
        return registedGenerator
            .computeIfAbsent(
                col, key -> new ConsistentCharsGenerator(col.getIntegerDigit(), '0', '9'))
            .nextValue();
      case CHAR:
      case VARCHAR:
      case VARCHAR2:
      case MEANS_ID:
        return registedGenerator
            .computeIfAbsent(
                col, key -> new ConsistentCharsGenerator(col.getIntegerDigit(), '0', 'z'))
            .nextValue();
      case DATE:
        return registedDate
            .computeIfAbsent(col, key -> LocalDate.now().minusMonths(2L))
            .plusDays(1L)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
      case MEANS_DATE:
        {
          LocalDate nextDate =
              registedDate
                  .computeIfAbsent(col, key -> LocalDate.now().minusMonths(2L))
                  .plusDays(1L);
          if (8 == col.getIntegerDigit()) {
            return nextDate.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
          }
          if (6 == col.getIntegerDigit()) {
            return nextDate.format(DateTimeFormatter.ofPattern("yyyyMM"));
          }
        }
      case TIMESTAMP:
        return registedDateTime
            .computeIfAbsent(col, key -> LocalDateTime.now().minusDays(10L))
            .plusHours(1L)
            .format(DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss.SSS"));
      default:
        return RandomValueUtils.generate(col);
    }
  }

  private class ConsistentCharsGenerator {

    private char[] valueChars;

    private char minCode;
    private char maxCode;

    private int length;

    public ConsistentCharsGenerator(int length, char minCode, char maxCode) {
      this.minCode = minCode;
      this.maxCode = maxCode;

      this.length = length;
      this.valueChars = new char[length];
      Arrays.fill(this.valueChars, minCode);
    }

    public String nextValue() {
      increment(length - 1);

      return String.valueOf(valueChars);
    }

    private void increment(int digit) {
      if (digit < 0) {
        return;
      }
      if ('a' <= maxCode) {
        if ('Z' == valueChars[digit]) {
          valueChars[digit] = 'a';
        } else if ('9' == valueChars[digit]) {
          valueChars[digit] = 'A';
        } else {
          valueChars[digit]++;
        }
      } else if ('A' <= maxCode) {
        if ('9' == valueChars[digit]) {
          valueChars[digit] = 'A';
        } else {
          valueChars[digit]++;
        }
      } else {
        valueChars[digit]++;
      }

      if (maxCode < valueChars[digit]) {
        valueChars[digit] = minCode;
        increment(digit - 1);
      }

      return;
    }
  }
}
