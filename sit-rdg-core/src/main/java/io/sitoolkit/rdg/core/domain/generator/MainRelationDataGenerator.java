package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainRelationDataGenerator extends RelationDataGenerator {

  RowDataStore dataStoreForMainRel = new RowDataStore();

  public MainRelationDataGenerator(
      RelationDef relation, RowDataStore dataStoreForSubRel, GeneratorConfig config) {
    super(relation, dataStoreForSubRel, config);
  }

  @Override
  public void doGenerateAndFill(RowData rowData) {
    RowData mainData = null;

    do {
      mainData = RowDataGenerator.append(rowData, relation, config);
    } while (dataStoreForMainRel.contains(mainData));

    dataStoreForMainRel.add(mainData);
    rowData.putAll(mainData);
    RowData subData = RowDataGenerator.replicateForSub(mainData, relation);

    log.trace("Generated sub data: {}", subData);

    dataStoreForSubRel.add(subData);
  }
}
