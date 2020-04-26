package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor
public abstract class RelationDataGenerator {

  protected Logger log = LoggerFactory.getLogger(getClass());

  @Getter(AccessLevel.PROTECTED)
  private final RelationDef relation;

  /** This dataStore is to store the data of this.relation.getSubTable() */
  @Getter(AccessLevel.PROTECTED)
  private final RowDataStore dataStoreForSubRel;

  @Getter(AccessLevel.PROTECTED)
  // TODO to be moved to columndef
  private final GeneratorConfig config;

  @Getter(AccessLevel.PROTECTED)
  @Setter(AccessLevel.PACKAGE)
  private UniqueDataStore uniqueDataStore;

  public void generateAndFill(RowData rowData) {
    log.trace("Generating data for {}", relation);

    doGenerateAndFill(rowData);

    log.trace("Generated data: {}", rowData);
    log.trace("Generated data for {}", relation);
  }

  protected abstract void doGenerateAndFill(RowData rowData);

  public void end() {
    // NOP
  }
}
