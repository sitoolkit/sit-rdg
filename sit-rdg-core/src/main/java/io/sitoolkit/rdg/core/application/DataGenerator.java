package io.sitoolkit.rdg.core.application;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import io.sitoolkit.rdg.core.domain.generator.GeneratedValueStore;
import io.sitoolkit.rdg.core.domain.generator.RandomValueRow;
import io.sitoolkit.rdg.core.domain.generator.TableComparator;
import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfigReader;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.CsvWriter;
import io.sitoolkit.rdg.core.infrastructure.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataGenerator {

  public List<Path> generate(Path input, Path output) throws IOException {

    SchemaInfo schemaInfo = JsonUtils.json2object(input.resolve("schema.json"), SchemaInfo.class);

    GeneratorConfig config = new GeneratorConfigReader().read(input);

    TableComparator comparator = new TableComparator(config);

    List<Path> outputs = new ArrayList<>();
    for (SchemaDef schema : schemaInfo.getSchemas()) {

      GeneratedValueStore store = new GeneratedValueStore(config);

      List<TableDef> tables =
          schema.getTables().stream().sorted(comparator::compare).collect(Collectors.toList());

      for (TableDef table : tables) {
        Integer rowCount = config.getRowCount(table);
        Path outPath = write(table, rowCount, output, store);
        log.info("Generated csv {}", outPath.toAbsolutePath());
        outputs.add(outPath);
      }
    }

    return outputs;
  }

  public Path write(TableDef table, Integer rowCount, Path out, GeneratedValueStore store)
      throws IOException {

    String schemaName = table.getSchemaName().orElse("UNKNOWN");
    String tableName = table.getName();
    String csvName = String.join(".", schemaName, tableName, "csv");

    return generate(table, rowCount, out.resolve(csvName), store);
  }

  public Path generate(TableDef tableDef, Integer rowCount, Path path, GeneratedValueStore store)
      throws IOException {

    List<ColumnDef> cols = tableDef.getColumns();
    List<ColumnDef> pks =
        cols.stream().filter(ColumnDef::isPrimaryKey).collect(Collectors.toList());
    List<Object> header = cols.stream().map(ColumnDef::getName).collect(Collectors.toList());

    try (CsvWriter writer = new CsvWriter(path)) {
      writer.writeAppend(header);

      for (int row = 1; row <= rowCount; row++) {

        Optional<RandomValueRow> generatedValueRow = store.generateRow(cols, pks, row);

        if (generatedValueRow.isPresent()) {
          List<Object> lineValues = generatedValueRow.get().getLineValues();
          writer.writeAppend(lineValues);
        }
      }
      writer.close();
    }

    //    store.clearGeneratedRowsCache();

    return path;
  }
}
