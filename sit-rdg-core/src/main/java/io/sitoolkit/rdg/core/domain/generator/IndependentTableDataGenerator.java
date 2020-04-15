package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.TableDef;

public class IndependentTableDataGenerator extends TableDataGenerator {

  public IndependentTableDataGenerator(TableDef table, GeneratorConfig config) {
    super(table, config);
  }

  public RowData generate() {
    RowData rowData = RowDataGenerator.generate(table, config);

    for (RelationDataGenerator generator : generators) {
      generator.generateAndFill(rowData);
    }

    return rowData;
  }
}
