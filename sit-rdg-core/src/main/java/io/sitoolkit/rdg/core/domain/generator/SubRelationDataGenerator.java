package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SubRelationDataGenerator extends RelationDataGenerator {

  public SubRelationDataGenerator(
      RelationDef relation, RowDataStore dataStoreForSubRel, GeneratorConfig config) {
    super(relation, dataStoreForSubRel, config);
  }

  @Override
  public void doGenerateAndFill(RowData rowData) {

    if (rowData.containsAsSub(getRelation())) {
      return;
    }

    List<UniqueConstraintDef> uniques = getRelation().getSubUniqueConstraints();

    log.trace("Check for {}", uniques);

    RowData storedData = null;
    int loopCount = 0;

    do {
      storedData = getDataStoreForSubRel().get();
      if (loopCount++ > 1000) {
        throw new IllegalStateException("Give up generating for " + getRelation());
      }

    } while (getUniqueDataStore().containsAny(uniques, storedData));

    getUniqueDataStore().putAll(uniques, storedData);
    rowData.putAll(storedData);
    log.trace("Get and add data from store: {}", storedData);
  }
}
