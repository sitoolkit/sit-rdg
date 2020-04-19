package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.generator.config.RelationConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.TableSorter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
              "{}({}) is registered for {}",
              relGenerator.getClass().getSimpleName(),
              relGenerator.getDataStoreForSubRel(),
              relGenerator.getRelation());
        }
      }
    }

    return generators;
  }

  static TableDataGenerator buildForIndependent(
      TableDef table, GeneratorConfig config, Map<RelationDef, RowDataStore> relStoreMap) {
    TableDataGenerator generator = new IndependentTableDataGenerator(table, config);

    for (RelationDef relation : table.getRelations()) {
      RowDataStore dataStore = buildDataStore(relation, config);
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
      RowDataStore dataStore = relStoreMap.get(relation);
      generator.add(new SubRelationDataGenerator(relation, dataStore, config));
    }

    for (RelationDef relation : table.getSelfRelations()) {
      RowDataStore dataStore = relStoreMap.computeIfAbsent(relation, rel -> new RowDataStoreImpl());
      generator.add(new SelfRelationDataGenerator(relation, dataStore, config));
    }

    for (RelationDef relation : table.getMainRelations()) {
      RowDataStore dataStore =
          relStoreMap.computeIfAbsent(relation, rel -> buildDataStore(relation, config));
      generator.add(new MainRelationDataGenerator(relation, dataStore, config));
    }

    return generator;
  }

  static RowDataStore buildDataStore(RelationDef relation, GeneratorConfig config) {
    Optional<RelationConfig> rconfig = config.findRelationConfig(relation.getRightColumns());

    if (rconfig.isEmpty()) {
      return new RowDataStoreImpl();
    }

    MultiRowDataStore dataStore = new MultiRowDataStore();
    dataStore.initialize(rconfig.get().getMultiplicities());
    return dataStore;
  }
}
