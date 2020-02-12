package io.sitoolkit.rdg.core.domain.generator;

import static com.google.common.truth.Truth.*;

import java.util.List;
import java.util.stream.IntStream;

import org.junit.Test;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.DataType;

public class ConsistentValueGeneratorTest {

  @Test
  public void testGenerate() {
    ConsistentValueGenerator generator = new ConsistentValueGenerator();
    ColumnDef numberColmn =
        ColumnDef.builder().name("NUMBER").dataType(DataType.NUMBER).args(List.of("5")).build();
    assertThat(generator.generate(numberColmn)).isEqualTo("00001");
    assertThat(generator.generate(numberColmn)).isEqualTo("00002");
    assertThat(generator.generate(numberColmn)).isEqualTo("00003");
    assertThat(generator.generate(numberColmn)).isEqualTo("00004");
    assertThat(generator.generate(numberColmn)).isEqualTo("00005");
    assertThat(generator.generate(numberColmn)).isEqualTo("00006");
    assertThat(generator.generate(numberColmn)).isEqualTo("00007");
    assertThat(generator.generate(numberColmn)).isEqualTo("00008");
    assertThat(generator.generate(numberColmn)).isEqualTo("00009");
    assertThat(generator.generate(numberColmn)).isEqualTo("00010");

    ColumnDef idColumn =
        ColumnDef.builder().name("ID").dataType(DataType.MEANS_ID).args(List.of("4")).build();
    assertThat(generator.generate(idColumn)).isEqualTo("0001");

    IntStream.range('1', '9').forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("000A");
    IntStream.range('A', 'Z').forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("000a");
    IntStream.range('a', 'z').forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("0010");

    IntStream.range('0', '9').forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("001A");
    IntStream.range('A', 'Z').forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("001a");
    IntStream.range('a', 'z').forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("0020");

    int oneRound = ('z' - 'a' + 1) + ('Z' - 'A' + 1) + ('9' - '0' + 1);
    IntStream.range(1, oneRound).forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("0030");

    IntStream.range(1, oneRound * oneRound).forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("0130");

    IntStream.range(1, oneRound * oneRound).forEach(not -> generator.generate(idColumn));
    assertThat(generator.generate(idColumn)).isEqualTo("0230");
  }
}
