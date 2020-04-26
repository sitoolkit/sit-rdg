package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;

public class SelfRelationDataGenerator extends RelationDataGenerator {

  public SelfRelationDataGenerator(
      RelationDef relation, RowDataStore dataStoreForSubRel, GeneratorConfig config) {
    super(relation, dataStoreForSubRel, config);
  }

  @Override
  public void doGenerateAndFill(RowData rowData) {

    if (rowData.containsAsSub(getRelation())) {
      return;
    }

    RowData generatedData = null;

    do {
      generatedData = RowDataGenerator.generateForSelfRelation(rowData, getRelation(), getConfig());
    } while (getDataStoreForSubRel().contains(generatedData));

    rowData.putAll(generatedData);
  }
}
