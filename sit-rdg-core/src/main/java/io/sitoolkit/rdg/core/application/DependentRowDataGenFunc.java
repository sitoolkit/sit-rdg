package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.generator.RowDataGenerator;
import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import java.util.function.Function;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@AllArgsConstructor
class DependentRowDataGenFunc implements Function<TableDef, RowData> {

  DataStore dataStore;
  GeneratorConfig config;

  @Override
  public RowData apply(TableDef table) {
    RowData rowData = new RowData();

    log.trace("====== Start generating data {}", table.getName());

    for (RelationDef relation : table.getSubRelations()) {

      log.trace("Generating sub relational data for {}", relation);

      if (rowData.containsAsSub(relation)) {
        continue;
      }

      RowData storedData = dataStore.get(relation);
      rowData.putAll(storedData);
      log.trace("Get and add data from store: {}", storedData);
    }

    for (RelationDef relation : table.getSelfRelations()) {

      log.trace("Generating self relational data for {}", relation);

      if (rowData.containsAsSub(relation)) {
        continue;
      }

      RowData generatedData = RowDataGenerator.generateForSelfRelation(rowData, relation, config);
      log.trace("Generate self relation data: {}", generatedData);
      rowData.putAll(generatedData);
    }

    for (RelationDef relation : table.getMainRelations()) {

      log.trace("Generating main relational data for {}", relation);

      RowData mainData = RowDataGenerator.append(rowData, relation, config);
      // TODO check unique constraint
      rowData.putAll(mainData);
      RowData subData = RowDataGenerator.replicateForSub(mainData, relation);

      log.trace("Generating main relational data: {}", subData);

      dataStore.put(relation, subData);
    }

    RowDataGenerator.fill(rowData, table, config);

    log.trace("Generated data: {}", rowData);

    return rowData;
  }
}
