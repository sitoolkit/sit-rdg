package io.sitoolkit.rdg.core.domain.generator;

import java.time.LocalDateTime;
import java.util.List;

import org.junit.Test;

import static com.google.common.truth.Truth.assertThat;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import static io.sitoolkit.rdg.core.domain.schema.ConstraintAttribute.PRIMARY_KEY;

public class RandomValueRowTest {

  @Test
  public void shouldHashcodesAreTheSameRegardlessOfOrder() {
    RandomValueRow nuturalorderColumns = new RandomValueRow();
    nuturalorderColumns.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "AAA");
    nuturalorderColumns.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME2")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "BBB");
    nuturalorderColumns.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME3").build(), "CCC");

    RandomValueRow reverseorderColumns = new RandomValueRow();
    reverseorderColumns.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME3").build(), "CCC");
    reverseorderColumns.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME2")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "BBB");
    reverseorderColumns.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "AAA");

    assertThat(nuturalorderColumns.hashCode()).isEqualTo(reverseorderColumns.hashCode());
  }

  @Test
  public void equalTest() {
    RandomValueRow row1 = new RandomValueRow();
    row1.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "AAA");
    row1.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME2")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "BBB");
    row1.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME3").build(), "CCC");

    RandomValueRow row2 = new RandomValueRow();
    row2.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "AAA");
    row2.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME3").build(), "CCC");
    row2.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME2")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "BBB");

    RandomValueRow row3 = new RandomValueRow();
    row3.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME3").build(), "CCC");
    row3.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "AAA");
    row3.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME2")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "CCC");

    RandomValueRow row4 = new RandomValueRow();
    row4.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "AAA");
    row4.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME2")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        "BBB");
    row4.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME3").build(), "CCC");

    RandomValueRow row5 = new RandomValueRow();
    row5.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        LocalDateTime.of(2012, 2, 12, 0, 0, 0));
    row5.put(
        ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME2").build(),
        LocalDateTime.of(2019, 1, 10, 2, 3, 1));

    RandomValueRow row6 = new RandomValueRow();
    row6.put(
        ColumnDef.builder()
            .fullyQualifiedName("SC.TBL.NAME1")
            .constraints(List.of(PRIMARY_KEY))
            .build(),
        LocalDateTime.of(2012, 2, 12, 0, 0, 0));
    row6.put(ColumnDef.builder().fullyQualifiedName("SC.TBL.NAME2").build(), LocalDateTime.now());

    assertThat(List.of(row1).contains(row2)).isTrue();
    assertThat(List.of(row3).contains(row4)).isFalse();
    assertThat(List.of(row5).contains(row6)).isTrue();
  }
}
