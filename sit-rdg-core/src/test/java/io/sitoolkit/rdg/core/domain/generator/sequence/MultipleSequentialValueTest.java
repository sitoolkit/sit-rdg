package io.sitoolkit.rdg.core.domain.generator.sequence;

import static com.google.common.truth.Truth.*;
import static io.sitoolkit.rdg.core.domain.schema.ConstraintAttribute.*;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.DataType;
import io.sitoolkit.rdg.core.domain.schema.DataTypeName;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;
import org.junit.Test;

public class MultipleSequentialValueTest {

  @Test
  public void testVarcharAndNumberSeq() {

    ColumnDef varcharColumn =
        ColumnDef.builder()
            .name("PK1")
            .fullyQualifiedName("SC.TABLE.PK1")
            .constraints(List.of(PRIMARY_KEY))
            .dataType(DataType.builder().name(DataTypeName.VARCHAR).size(1).build())
            .build();
    ColumnDef numberColumn =
        ColumnDef.builder()
            .name("PK2")
            .fullyQualifiedName("SC.TABLE.PK2")
            .constraints(List.of(PRIMARY_KEY))
            .dataType(DataType.builder().name(DataTypeName.DECIMAL).size(1).build())
            .build();

    MultipleSequentialValue multipleSeq =
        new MultipleSequentialValue(new ArrayList<>(List.of(varcharColumn, numberColumn)));

    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("0");
    assertThat(multipleSeq.currentVal()).isEqualTo("0");

    assertThat(multipleSeq.nextVal()).isEqualTo("1");
    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("1");

    IntStream.range('1', '9').forEach(not -> multipleSeq.nextVal());
    assertThat(multipleSeq.nextVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("1");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("0");

    int oneRound = ('9' - '0' + 1) * 9;
    IntStream.range(1, oneRound).forEach(not -> multipleSeq.nextVal());

    assertThat(multipleSeq.nextVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("A");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("0");
  }

  @Test
  public void testDateAndDateTimeSeq() {
    ColumnDef dateColumn =
        ColumnDef.builder()
            .fullyQualifiedName("SC.TABLE.PK1")
            .constraints(List.of(PRIMARY_KEY))
            .dataType(DataType.builder().name(DataTypeName.DATE).size(1).build())
            .build();
    ColumnDef dateTimeColumn =
        ColumnDef.builder()
            .fullyQualifiedName("SC.TABLE.PK2")
            .constraints(List.of(PRIMARY_KEY))
            .dataType(DataType.builder().name(DataTypeName.TIMESTAMP).size(1).build())
            .build();

    MultipleSequentialValue multipleSeq =
        new MultipleSequentialValue(new ArrayList<>(List.of(dateColumn, dateTimeColumn)));

    LocalDate today = LocalDate.now();
    LocalDateTime todayandtime = LocalDate.now().atTime(0, 0, 0);
    DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter datetimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    LocalDate startDate = today.minusMonths(7L);
    LocalDate endDate = today.plusMonths(1L);
    LocalDateTime startDateTime = todayandtime.minusMonths(7L);

    assertThat(multipleSeq.getSequenceByPkColumn(dateColumn).currentVal())
        .isEqualTo(startDate.format(dateFormat));
    assertThat(multipleSeq.getSequenceByPkColumn(dateTimeColumn).currentVal())
        .isEqualTo(startDateTime.format(datetimeFormat));
    assertThat(multipleSeq.currentVal()).isEqualTo(startDateTime.format(datetimeFormat));

    assertThat(multipleSeq.nextVal()).isNotEqualTo(startDateTime.format(datetimeFormat));
    assertThat(multipleSeq.getSequenceByPkColumn(dateColumn).currentVal())
        .isEqualTo(startDate.format(dateFormat));
    assertThat(multipleSeq.getSequenceByPkColumn(dateTimeColumn).currentVal())
        .isNotEqualTo(startDateTime.format(datetimeFormat));

    int minutes = 60;
    int hour = 1 * minutes;
    int oneday = 24 * hour;
    int peek2peek = oneday * (int) ChronoUnit.DAYS.between(startDate, endDate);
    int tickSize = 12 * hour;
    int oneRound = peek2peek / tickSize;

    IntStream.range(1, oneRound).forEach(not -> multipleSeq.nextVal());

    assertThat(multipleSeq.nextVal()).isEqualTo(startDateTime.format(datetimeFormat));
    assertThat(multipleSeq.getSequenceByPkColumn(dateColumn).currentVal())
        .isNotEqualTo(startDate.format(dateFormat));
    assertThat(multipleSeq.getSequenceByPkColumn(dateTimeColumn).currentVal())
        .isEqualTo(startDateTime.format(datetimeFormat));
  }

  @Test
  public void shouldBeIncrement4position() {

    ColumnDef varcharColumn =
        ColumnDef.builder()
            .name("PK1")
            .fullyQualifiedName("SC.TABLE.PK1")
            .constraints(List.of(PRIMARY_KEY))
            .dataType(DataType.builder().name(DataTypeName.VARCHAR).size(1).build())
            .build();
    ColumnDef numberColumn =
        ColumnDef.builder()
            .name("PK2")
            .fullyQualifiedName("SC.TABLE.PK2")
            .constraints(List.of(PRIMARY_KEY))
            .dataType(DataType.builder().name(DataTypeName.DECIMAL).size(1).build())
            .build();

    MultipleSequentialValue multipleSeq =
        new MultipleSequentialValue(new ArrayList<>(List.of(varcharColumn, numberColumn)));

    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("0");
    assertThat(multipleSeq.currentVal()).isEqualTo("0");

    assertThat(multipleSeq.nextVal()).isEqualTo("1");
    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("1");

    multipleSeq.putIfPresent(varcharColumn, "A");
    multipleSeq.putIfPresent(numberColumn, "9");

    assertThat(multipleSeq.nextVal()).isEqualTo("0");
    assertThat(multipleSeq.getSequenceByPkColumn(varcharColumn).currentVal()).isEqualTo("B");
    assertThat(multipleSeq.getSequenceByPkColumn(numberColumn).currentVal()).isEqualTo("0");
  }
}
