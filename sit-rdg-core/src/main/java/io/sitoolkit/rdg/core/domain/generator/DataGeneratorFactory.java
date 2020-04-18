package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.TableSorter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DataGeneratorFactory {

  public static List<TableDataGenerator> build(List<TableDef> tables, GeneratorConfig config) {
    List<TableDataGenerator> generators = new ArrayList<>();

    List<TableDef> sortedTables = TableSorter.sortByDependency(tables);

    Map<RelationDef, RowDataStore> relStoreMap = new HashMap<>();

    for (TableDef table : sortedTables) {

      if (table.isDependent()) {
        generators.add(buildForDependent(table, config, relStoreMap));
      } else {
        generators.add(buildForIndependent(table, config, relStoreMap));
      }
    }

    if (log.isDebugEnabled()) {
      for (TableDataGenerator generator : generators) {
        log.debug(
            "{} is registered for {}",
            generator.getClass().getSimpleName(),
            generator.getTableName());

        for (RelationDataGenerator relGenerator : generator.getGenerators()) {
          log.debug(
              "{} is registered for {}",
              relGenerator.getClass().getSimpleName(),
              generator.getTableName());
        }
      }
    }

    return generators;
  }

  static TableDataGenerator buildForIndependent(
      TableDef table, GeneratorConfig config, Map<RelationDef, RowDataStore> relStoreMap) {
    TableDataGenerator generator = new IndependentTableDataGenerator(table, config);

    for (RelationDef relation : table.getRelations()) {
      RowDataStore dataStore = new RowDataStore();
      RelationDataGenerator relGen = new TopRelationDataGenerator(relation, dataStore, config);
      relStoreMap.put(relation, dataStore);
      generator.add(relGen);
    }

    return generator;
  }

  static TableDataGenerator buildForDependent(
      TableDef table, GeneratorConfig config, Map<RelationDef, RowDataStore> relStoreMap) {
    TableDataGenerator generator = new DependendTableDataGenerator(table, config);

    for (RelationDef relation : table.getSubRelations()) {
      RowDataStore dataStore = relStoreMap.computeIfAbsent(relation, rel -> new RowDataStore());
      generator.add(new SubRelationDataGenerator(relation, dataStore, config));
    }

    for (RelationDef relation : table.getSelfRelations()) {
      RowDataStore dataStore = relStoreMap.computeIfAbsent(relation, rel -> new RowDataStore());
      generator.add(new SelfRelationDataGenerator(relation, dataStore, config));
    }

    for (RelationDef relation : table.getMainRelations()) {
      RowDataStore dataStore = relStoreMap.computeIfAbsent(relation, rel -> new RowDataStore());
      generator.add(new MainRelationDataGenerator(relation, dataStore, config));
    }

    return generator;
  }
}
