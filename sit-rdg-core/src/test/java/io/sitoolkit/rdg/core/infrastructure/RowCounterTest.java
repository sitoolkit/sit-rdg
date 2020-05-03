package io.sitoolkit.rdg.core.infrastructure;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Test;

public class RowCounterTest {

  @Test
  public void test() {
    RowCounter rowCounter = new RowCounter();
    rowCounter.init(500, 10, 0);

    assertThat(rowCounter.isCheckPoint(0), is(false));
    assertThat(rowCounter.isCheckPoint(1), is(false));
    assertThat(rowCounter.isCheckPoint(50), is(true));

    rowCounter.next();

    assertThat(rowCounter.isCheckPoint(50), is(false));
    assertThat(rowCounter.isCheckPoint(100), is(true));

    assertThat(rowCounter.getProgressRate(50), is(10));
    assertThat(rowCounter.getProgressRate(100), is(20));
  }
}
