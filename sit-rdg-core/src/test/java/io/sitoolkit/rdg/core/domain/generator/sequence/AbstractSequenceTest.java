package io.sitoolkit.rdg.core.domain.generator.sequence;

import static com.google.common.truth.Truth.*;

import org.junit.Test;

public class AbstractSequenceTest {

  @Test
  public void textSequence() {
    SequentialValue count1to10 = new SequentialValue(1);

    assertThat(count1to10.isReachTop()).isFalse();
    assertThat(count1to10.currentVal()).isEqualTo("1");
    assertThat(count1to10.nextVal()).isEqualTo("2");
    assertThat(count1to10.nextVal()).isEqualTo("3");
    assertThat(count1to10.nextVal()).isEqualTo("4");
    assertThat(count1to10.nextVal()).isEqualTo("5");
    assertThat(count1to10.nextVal()).isEqualTo("6");
    assertThat(count1to10.nextVal()).isEqualTo("7");
    assertThat(count1to10.nextVal()).isEqualTo("8");
    assertThat(count1to10.nextVal()).isEqualTo("9");

    assertThat(count1to10.isReachTop()).isFalse();
    assertThat(count1to10.nextVal()).isEqualTo("10");
    assertThat(count1to10.isReachTop()).isTrue();
  }

  public class SequentialValue extends AbstractSequence {

    private int count;

    public SequentialValue(int count) {
      this.count = count;
    }

    @Override
    public boolean isReachTop() {
      return 10 == count;
    }

    @Override
    public String currentVal() {
      return String.valueOf(count);
    }

    @Override
    public String nextVal() {
      count++;
      return currentVal();
    }

    @Override
    public void initVal() {
      count = 0;
    }
  }
}
