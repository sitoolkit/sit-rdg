package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MainRelationDataGenerator extends RelationDataGenerator {

  public MainRelationDataGenerator(
      RelationDef relation, RowDataStore dataStoreForSubRel, GeneratorConfig config) {
    super(relation, dataStoreForSubRel, config);
  }

  @Override
  public void doGenerateAndFill(RowData rowData) {
    RowData mainData = null;

    List<UniqueConstraintDef> uniques = getRelation().getMainUniqueConstraints();

    do {
      mainData = RowDataGenerator.append(rowData, getRelation(), getConfig());
    } while (getUniqueDataStore().containsAny(uniques, mainData));

    getUniqueDataStore().putAll(uniques, mainData);
    rowData.putAll(mainData);
    RowData subData = RowDataGenerator.replicateForSub(mainData, getRelation());

    log.trace("Generated main data: {} and sub data: {}", mainData, subData);

    getDataStoreForSubRel().add(subData);
  }
}
