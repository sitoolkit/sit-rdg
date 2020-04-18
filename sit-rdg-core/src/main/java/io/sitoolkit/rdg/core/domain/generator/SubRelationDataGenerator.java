package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.List;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubRelationDataGenerator extends RelationDataGenerator {

  public SubRelationDataGenerator(
      RelationDef relation, RowDataStore dataStoreForSubRel, GeneratorConfig config) {
    super(relation, dataStoreForSubRel, config);
  }

  @Override
  public void doGenerateAndFill(RowData rowData) {

    if (rowData.containsAll(getRelation().getRightColumns())) {
      return;
    }

    List<UniqueConstraintDef> uniques = getRelation().getSubUniqueConstraints();
    RowData storedData = null;

    if (uniques.isEmpty()) {
      storedData = getDataStoreForSubRel().get();

    } else {
      Function<UniqueConstraintDef, RowData> function = unique -> getDataStoreForSubRel().get();

      storedData = RowDataGenerator.applyWithUniqueCheck(function, uniques, getUniqueDataStore());
    }

    rowData.putAll(storedData);
    log.trace("Get and add data from store: {}", storedData);
  }
}
