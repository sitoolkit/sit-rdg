package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.generator.config.ValueGenerator;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.List;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RowDataGenerator {

  public static RowData generate(
      TableDef table, UniqueDataStore dataStore, GeneratorConfig config) {

    RowData rowData = new RowData();

    int size = table.getUniqueConstraints().size();

    for (int i = 0; i < size; i++) {
      UniqueConstraintDef unique = table.getUniqueConstraints().get(i);

      RowData uniqueData = new RowData();

      for (ColumnDef column : unique.getColumns()) {
        ValueGenerator generator = config.findValueGenerator(column);
        String generatedValue = generator.generate(column);
        uniqueData.put(column, generatedValue);
      }

      if (dataStore.contains(unique, uniqueData)) {
        i -= 1;
      } else {
        rowData.putAll(uniqueData);
        dataStore.put(unique, uniqueData);
      }
    }

    fill(rowData, table, config);

    return rowData;
  }

  public static RowData generate(TableDef table, GeneratorConfig config) {
    RowData rowData = new RowData();

    for (ColumnDef column : table.getColumns()) {
      ValueGenerator generator = config.findValueGenerator(column);
      String generatedValue = generator.generate(column);
      rowData.put(column, generatedValue);
    }

    return rowData;
  }

  public static RowData generateUnrelated(
      TableDef table, RelationDef relation, GeneratorConfig config) {
    RowData rowData = new RowData();

    for (ColumnDef column : table.getColumns()) {
      if (!relation.getDistinctColumns().contains(column)) {
        continue;
      }
      ValueGenerator generator = config.findValueGenerator(column);
      String generatedValue = generator.generate(column);
      rowData.put(column, generatedValue);
    }

    return rowData;
  }

  public static RowData generateForSelfRelation(
      RowData rowData, RelationDef relation, GeneratorConfig config) {
    RowData newData = new RowData();

    for (ColumnPair pair : relation.getColumnPairs()) {
      ColumnDef main = pair.getMain();
      ColumnDef sub = pair.getSub();
      String mainValue = rowData.get(main);
      String subValue = rowData.get(sub);

      if (mainValue == null) {

        if (subValue == null) {
          ValueGenerator generator = config.findValueGenerator(main);
          mainValue = generator.generate(main);
          newData.put(main, mainValue);
          newData.put(sub, mainValue);
        } else {
          newData.put(main, subValue);
        }

      } else {

        if (subValue == null) {
          newData.put(sub, mainValue);
        }
      }
    }

    return newData;
  }

  // void hoge(RowData rowData, ColumnDef column, GeneratorConfig config) {

  // }

  public static RowData generateForSub(RelationDef relation, GeneratorConfig config) {
    RowData rowData = new RowData();

    for (ColumnPair pair : relation.getColumnPairs()) {
      // TODO for primary key column
      ColumnDef column = pair.getSub();
      ValueGenerator generator = config.findValueGenerator(column);
      rowData.put(column, generator.generate(column));
    }

    return rowData;
  }

  public static RowData replicateForSub(RowData rowData, RelationDef relation) {
    RowData replica = new RowData();
    for (ColumnPair pair : relation.getColumnPairs()) {
      replica.put(pair.getSub(), rowData.get(pair.getMain()));
    }
    return replica;
  }

  public static RowData filter(RowData rowData, RelationDef relation) {
    RowData filterd = new RowData();
    for (ColumnPair pair : relation.getColumnPairs()) {
      filterd.put(pair.getMain(), rowData.get(pair.getMain()));
    }
    return filterd;
  }

  public static void fill(RowData rowData, TableDef table, GeneratorConfig config) {
    for (ColumnDef column : table.getColumns()) {
      if (rowData.contains(column)) {
        continue;
      }

      ValueGenerator generator = config.findValueGenerator(column);
      rowData.put(column, generator.generate(column));
    }
  }

  public static RowData takeOverGenerate(
      RowData rowData, RelationDef parentRelation, RelationDef relation, GeneratorConfig config) {
    RowData newRowData = new RowData();

    for (ColumnPair parentPair : parentRelation.getColumnPairs()) {
      newRowData.put(parentPair.getSub(), rowData.get(parentPair.getMain()));
    }

    for (ColumnPair pair : relation.getColumnPairs()) {
      ColumnDef main = pair.getMain();
      if (newRowData.contains(main)) {
        continue;
      }
      ValueGenerator generator = config.findValueGenerator(main);
      newRowData.put(main, generator.generate(main));
    }

    return newRowData;
  }

  public static RowData generateAndFill(
      RowData rowData, RelationDef relation, GeneratorConfig config) {
    RowData appended = new RowData();

    for (ColumnPair pair : relation.getColumnPairs()) {
      if (rowData.contains(pair.getMain())) {
        appended.put(pair.getSub(), rowData.get(pair.getMain()));
        continue;
      }

      ValueGenerator generator = config.findValueGenerator(pair.getMain());
      String value = generator.generate(pair.getMain());
      rowData.put(pair.getMain(), value);
      appended.put(pair.getSub(), value);
    }

    return appended;
  }

  public static RowData append(RowData rowData, RelationDef relation, GeneratorConfig config) {
    RowData appended = new RowData();

    for (ColumnPair pair : relation.getColumnPairs()) {
      ColumnDef main = pair.getMain();
      if (rowData.contains(main)) {
        appended.put(main, rowData.get(main));
        continue;
      }

      ValueGenerator generator = config.findValueGenerator(main);
      String value = generator.generate(main);
      appended.put(main, value);
    }

    return appended;
  }

  public static RowData append(
      RowData rowData, UniqueConstraintDef unique, GeneratorConfig config) {
    RowData append = new RowData();

    for (ColumnDef column : unique.getColumns()) {
      String value = rowData.get(column);
      if (value == null) {
        ValueGenerator generator = config.findValueGenerator(column);
        value = generator.generate(column);
      }

      append.put(column, value);
    }

    return append;
  }

  public static RowData applyWithUniqueCheck(
      Function<UniqueConstraintDef, RowData> function,
      List<UniqueConstraintDef> uniques,
      UniqueDataStore uniqueDataStore) {

    RowData rowData = new RowData();
    for (UniqueConstraintDef unique : uniques) {
      rowData.putAll(applyWithUniqueCheck(function, unique, uniqueDataStore));
    }

    return rowData;
  }

  public static RowData applyWithUniqueCheck(
      Function<UniqueConstraintDef, RowData> function,
      UniqueConstraintDef unique,
      UniqueDataStore uniqueDataStore) {

    log.trace("Applying {} with checking {}", function, unique);

    RowData rowData = null;

    int loopCount = 0;
    do {
      rowData = function.apply(unique);

      loopCount++;
      if (loopCount > 10) {
        throw new RetryException("Give up generating for " + unique + ", " + rowData);
      }

    } while (uniqueDataStore.contains(unique, rowData));

    log.trace(
        "Generated unique data: {}({}) for {} in {}",
        rowData,
        rowData.hashCode(),
        unique,
        uniqueDataStore);

    uniqueDataStore.put(unique, rowData);

    return rowData;
  }
}
