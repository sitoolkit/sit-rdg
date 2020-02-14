package io.sitoolkit.rdg.core.domain.generator.sequence;

import static com.google.common.truth.Truth.*;

import java.util.stream.IntStream;

import org.junit.Test;

public class SequentialStringTest {

  @Test
  public void testNextVal() {
    SequentialString seq0to9 = new SequentialString(5, '0', '9');
    assertThat(seq0to9.nextVal()).isEqualTo("00001");
    assertThat(seq0to9.nextVal()).isEqualTo("00002");
    assertThat(seq0to9.nextVal()).isEqualTo("00003");
    assertThat(seq0to9.nextVal()).isEqualTo("00004");
    assertThat(seq0to9.nextVal()).isEqualTo("00005");
    assertThat(seq0to9.nextVal()).isEqualTo("00006");
    assertThat(seq0to9.nextVal()).isEqualTo("00007");
    assertThat(seq0to9.nextVal()).isEqualTo("00008");
    assertThat(seq0to9.nextVal()).isEqualTo("00009");
    assertThat(seq0to9.nextVal()).isEqualTo("00010");

    SequentialString seq0toz = new SequentialString(4, '0', 'z');

    assertThat(seq0toz.nextVal()).isEqualTo("0001");
    IntStream.range('1', '9').forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("000A");
    IntStream.range('A', 'Z').forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("000a");
    IntStream.range('a', 'z').forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("0010");

    IntStream.range('0', '9').forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("001A");
    IntStream.range('A', 'Z').forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("001a");
    IntStream.range('a', 'z').forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("0020");

    int oneRound = ('z' - 'a' + 1) + ('Z' - 'A' + 1) + ('9' - '0' + 1);
    IntStream.range(1, oneRound).forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("0030");

    IntStream.range(1, oneRound * oneRound).forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("0130");

    IntStream.range(1, oneRound * oneRound).forEach(not -> seq0toz.nextVal());
    assertThat(seq0toz.nextVal()).isEqualTo("0230");
  }
}
