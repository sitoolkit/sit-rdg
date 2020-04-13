package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.RowData;
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
      // rowData.putAllMainToSub(storedData, relation);
      rowData.putAll(storedData);

      log.trace("Get data from stored: {}", storedData);
    }

    for (RelationDef relation : table.getSelfRelations()) {

      log.trace("Generating self relational data for {}", relation);

      if (rowData.containsAsSub(relation)) {
        continue;
      }

      rowData.putAll(RowDataGenerator.generate(relation, config));
      log.trace("Generationg data: {}", rowData);
    }

    for (RelationDef relation : table.getMainRelations()) {

      log.trace("Generating main relational data for {}", relation);

      RowData relatedData = RowDataGenerator.generateAndFill(rowData, relation, config);
      dataStore.put(relation, relatedData);
      // rowData.putAll(relatedData);

      // if (table.equals(relation.getLeftTable())) {

      // Optional<RelationDef> parentOpt = relation.getParent();

      // if (parentOpt.isPresent()) {
      //   RowData storedData = dataStore.get(parentOpt.get());
      //   RowData newRelatedData =
      //       RowDataGenerator.takeOverGenerate(storedData, parentOpt.get(), relation, config);
      //   dataStore.put(relation, newRelatedData);
      //   rowData.putAll(newRelatedData);

      //   log.trace("Get data from store: {}, append data: {}", storedData, newRelatedData);

      // } else {

      //   RowData relatedData = RowDataGenerator.generate(relation, config);
      //   dataStore.put(relation, relatedData);
      //   rowData.putAll(relatedData);

      //   log.trace("Generate data: {}", relatedData);
      // }

      // } else {
      //   RowData storedData = dataStore.get(relation);
      //   rowData.putAllMainToSub(storedData, relation);

      //   log.trace("Get data from stored: {}", storedData);
      // }
    }

    RowDataGenerator.fill(rowData, table, config);

    log.trace("Generated data: {}", rowData);

    return rowData;
  }
}
