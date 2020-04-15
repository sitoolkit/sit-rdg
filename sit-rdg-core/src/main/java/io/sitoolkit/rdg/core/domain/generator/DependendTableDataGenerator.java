package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DependendTableDataGenerator extends TableDataGenerator {

  public DependendTableDataGenerator(TableDef table, GeneratorConfig config) {
    super(table, config);
  }

  public RowData generate() {
    RowData rowData = new RowData();

    for (RelationDataGenerator generator : generators) {
      generator.generateAndFill(rowData);
    }

    RowDataGenerator.fill(rowData, table, config);

    log.trace("Generated data: {}", rowData);

    return rowData;
  }
}
