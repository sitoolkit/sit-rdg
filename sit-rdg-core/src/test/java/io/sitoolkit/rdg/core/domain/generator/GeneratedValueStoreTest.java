package io.sitoolkit.rdg.core.domain.generator;

import static com.google.common.truth.Truth.*;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.generator.sequence.MultipleSequentialValue;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.DataType;
import io.sitoolkit.rdg.core.domain.schema.DataTypeName;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import java.util.List;
import java.util.function.Function;
import org.junit.Test;

public class GeneratedValueStoreTest {

  GeneratedValueStore store = new GeneratedValueStore(new GeneratorConfig());

  @Test
  public void shouldGenerateSameValue4SameColumn() {
    // select ... from table1, table2, table3
    // where table1.group1_column = table2.group1_column
    // and  table1.group2_column = table3.group2_column
    TableDef expctTable1 = new TableDef();
    expctTable1.setName("TABLE1");
    TableDef expctTable2 = new TableDef();
    expctTable2.setName("TABLE2");
    TableDef expctTable3 = new TableDef();
    expctTable3.setName("TABLE3");

    ColumnDef group1columnInTable1 = createColumnAbout(expctTable1).apply("GRUOP1_COLUMN");
    ColumnDef group2columnInTable1 = createColumnAbout(expctTable1).apply("GRUOP2_COLUMN");
    ColumnDef group1columnInTable2 = createColumnAbout(expctTable2).apply("GRUOP1_COLUMN");
    ColumnDef group2columnInTable3 = createColumnAbout(expctTable3).apply("GRUOP2_COLUMN");

    expctTable1.setColumns(List.of(group1columnInTable1, group2columnInTable1));
    expctTable2.setColumns(List.of(group1columnInTable2));
    expctTable3.setColumns(List.of(group2columnInTable3));

    ColumnPair betweenTable1andTable2 = new ColumnPair(group1columnInTable1, group1columnInTable2);
    ColumnPair betweenTable1andTable3 = new ColumnPair(group2columnInTable1, group2columnInTable3);

    RelationDef table1relation1 = new RelationDef();
    table1relation1.getColumnPairs().add(betweenTable1andTable2);
    table1relation1.getColumnPairs().add(betweenTable1andTable3);
    RelationDef table1relation2 = new RelationDef();
    table1relation2.getColumnPairs().add(betweenTable1andTable3);
    table1relation2.getColumnPairs().add(betweenTable1andTable2);
    RelationDef table2relation = new RelationDef();
    table2relation.getColumnPairs().add(betweenTable1andTable2);
    table2relation.getColumnPairs().add(betweenTable1andTable3);
    RelationDef table3relation = new RelationDef();
    table3relation.getColumnPairs().add(betweenTable1andTable3);
    table3relation.getColumnPairs().add(betweenTable1andTable2);

    group1columnInTable1.setRelations(List.of(table1relation1));
    group2columnInTable1.setRelations(List.of(table1relation2));
    group1columnInTable2.setRelations(List.of(table2relation));
    group2columnInTable3.setRelations(List.of(table3relation));

    MultipleSequentialValue g1t1Sequence =
        new MultipleSequentialValue(List.of(group1columnInTable1));
    MultipleSequentialValue g2t1Sequence =
        new MultipleSequentialValue(List.of(group2columnInTable1));
    MultipleSequentialValue g1t2Sequence =
        new MultipleSequentialValue(List.of(group1columnInTable2));
    MultipleSequentialValue g2t3Sequence =
        new MultipleSequentialValue(List.of(group2columnInTable3));

    String group1firstValue = store.putIfAbsent(group1columnInTable1, 1, g1t1Sequence);
    String group2firstValue = store.putIfAbsent(group2columnInTable1, 1, g2t1Sequence);
    String group1sameValue = store.putIfAbsent(group1columnInTable2, 1, g1t2Sequence);
    String group2sameValue = store.putIfAbsent(group2columnInTable3, 1, g2t3Sequence);

    assertThat(group1firstValue).isEqualTo(group1sameValue);
    assertThat(group2firstValue).isEqualTo(group2sameValue);
  }

  private Function<String, ColumnDef> createColumnAbout(TableDef table) {
    return columnName -> {
      return ColumnDef.builder()
          .table(table)
          .name(columnName)
          .dataType(DataType.builder().name(DataTypeName.DECIMAL).size(2).build())
          .build();
    };
  }
}
