package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.function.Function;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DependendTableDataGenerator extends TableDataGenerator {

  public DependendTableDataGenerator(TableDef table, GeneratorConfig config) {
    super(table, config);
  }

  public RowData generate() {
    RowData rowData = new RowData();

    for (RelationDataGenerator generator : getGenerators()) {
      generator.generateAndFill(rowData);
    }

    Function<UniqueConstraintDef, RowData> function =
        unique -> RowDataGenerator.append(rowData, unique, getConfig());

    RowData append =
        RowDataGenerator.applyWithUniqueCheck(
            function, getUnrelatedUnieuqeConstraints(), getUniqueDataStore());

    rowData.putAll(append);

    RowDataGenerator.fill(rowData, getTable(), getConfig());

    log.trace("Generated data: {}", rowData);

    return rowData;
  }
}
