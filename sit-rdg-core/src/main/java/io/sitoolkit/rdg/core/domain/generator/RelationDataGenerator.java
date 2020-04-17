package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class RelationDataGenerator {

  protected Logger log = LoggerFactory.getLogger(getClass());

  protected final RelationDef relation;

  /** This dataStore is to store the data of this.relation.getSubTable() */
  protected final RowDataStore dataStoreForSubRel;

  // TODO to be moved to columndef
  protected final GeneratorConfig config;

  protected UniqueDataStore uniqueDataStore = new UniqueDataStore();

  public void generateAndFill(RowData rowData) {
    log.trace("Generating data for {}", relation);

    doGenerateAndFill(rowData);

    log.trace("Generated data: {}", rowData);
    log.trace("Generated data for {}", relation);
  }

  protected abstract void doGenerateAndFill(RowData rowData);
}
