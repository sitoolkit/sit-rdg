package io.sitoolkit.rdg.core.domain.generator.sequence;

import static com.google.common.truth.Truth.*;

import java.time.LocalDate;
import java.util.stream.IntStream;

import org.junit.Test;

public class SequentialDateTest {

  @Test
  public void testNextVal() {
    LocalDate minDate = LocalDate.parse("2020-02-01");
    LocalDate maxDate = LocalDate.parse("2020-02-29");

    SequentialDate seqDate = new SequentialDate(minDate, maxDate, "yyyy-MM-dd");
    assertThat(seqDate.nextVal()).isEqualTo("2020-02-02");

    IntStream.range(1, 28).forEach(not -> seqDate.nextVal());
    assertThat(seqDate.currentVal()).isEqualTo("2020-02-29");
    assertThat(seqDate.nextVal()).isEqualTo("2020-02-01");
  }
}
