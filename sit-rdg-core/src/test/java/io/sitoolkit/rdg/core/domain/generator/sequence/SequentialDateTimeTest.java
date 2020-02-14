package io.sitoolkit.rdg.core.domain.generator.sequence;

import static com.google.common.truth.Truth.*;

import java.time.LocalDateTime;
import java.util.stream.IntStream;

import org.junit.Test;

public class SequentialDateTimeTest {

  @Test
  public void testNextVal() {
    LocalDateTime minDateTime = LocalDateTime.parse("2020-02-01T12:00:00");
    LocalDateTime maxDateTime = LocalDateTime.parse("2020-02-02T12:00:00");

    SequentialDateTime seqDateTime =
        new SequentialDateTime(minDateTime, maxDateTime, "yyyy-MM-dd HH:mm:ss");

    assertThat(seqDateTime.nextVal()).isEqualTo("2020-02-02 00:00:00");

    int minutes = 60;
    int hour = 1 * minutes;
    int oneday = 24 * hour;
    int peek2peek = 1 * oneday;
    int tickSize = 12 * hour;
    int oneRound = peek2peek / tickSize;
    IntStream.range(1, oneRound).forEach(not -> seqDateTime.nextVal());
    assertThat(seqDateTime.currentVal()).isEqualTo("2020-02-02 12:00:00");
    assertThat(seqDateTime.nextVal()).isEqualTo("2020-02-01 12:00:00");
  }
}
