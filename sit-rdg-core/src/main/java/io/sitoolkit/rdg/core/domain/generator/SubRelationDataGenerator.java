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

    if (rowData.containsAsSub(relation)) {
      return;
    }

    // TODO optimize
    List<UniqueConstraintDef> uniques = relation.getRightUniqueConstraints();

    RowData storedData = null;
    int loopCount = 0;

    do {
      storedData = dataStoreForSubRel.get();
      if (loopCount++ > 1000) {
        log.warn("Give up generating for {}", relation);
        return;
      }

    } while (uniqueDataStore.containsAny(uniques, storedData));

    uniqueDataStore.putAll(uniques, storedData);
    rowData.putAll(storedData);
    log.trace("Get and add data from store: {}", storedData);
  }
}
