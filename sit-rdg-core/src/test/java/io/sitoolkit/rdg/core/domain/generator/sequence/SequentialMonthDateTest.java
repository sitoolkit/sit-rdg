package io.sitoolkit.rdg.core.domain.generator.sequence;

import static org.hamcrest.CoreMatchers.is;

import java.time.LocalDate;

import org.junit.Assert;
import org.junit.Test;

public class SequentialMonthDateTest {

  @Test
  public void test() {

    LocalDate end = LocalDate.of(2020,7,1);
    LocalDate start = end.minusMonths(7L).plusDays(1L);

    SequentialMonthDate smd = new SequentialMonthDate(start, end, "yyyyMM");

    String current = smd.currentVal();
    String next1 = smd.nextVal();
    String next2 = smd.nextVal();
    String next3 = smd.nextVal();
    String next4 = smd.nextVal();
    String next5 = smd.nextVal();
    String next6 = smd.nextVal();
    String next7 = smd.nextVal();
    String nextOver = smd.nextVal();

    Assert.assertThat(current, is("201912"));
    Assert.assertThat(next1, is("202001"));
    Assert.assertThat(next2, is("202002"));
    Assert.assertThat(next3, is("202003"));
    Assert.assertThat(next4, is("202004"));
    Assert.assertThat(next5, is("202005"));
    Assert.assertThat(next6, is("202006"));
    Assert.assertThat(next7, is("202007"));
    Assert.assertThat(nextOver, is("201912"));
  }
}