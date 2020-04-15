package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubRelationDataGenerator extends RelationDataGenerator {

  public SubRelationDataGenerator(
      RelationDef relation, RowDataStore dataStoreForSubRel, GeneratorConfig config) {
    super(relation, dataStoreForSubRel, config);
  }

  @Override
  public void doGenerateAndFill(RowData rowData) {

    if (rowData.containsAsSub(relation)) {
      return;
    }

    RowData storedData = dataStoreForSubRel.get();
    rowData.putAll(storedData);
    log.trace("Get and add data from store: {}", storedData);
  }
}
