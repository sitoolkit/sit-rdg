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
import io.sitoolkit.rdg.core.infrastructure.DataWriter;
import io.sitoolkit.rdg.core.infrastructure.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataGenerator {

  public List<Path> generate(Path input, List<Path> output) throws IOException {

    SchemaInfo schemaInfo = JsonUtils.json2object(input.resolve("schema.json"), SchemaInfo.class);

    GeneratorConfig config = new GeneratorConfigReader().read(input);

    TableComparator comparator = new TableComparator(config);

    List<SchemaDef> schemas = new ArrayList<>(schemaInfo.getSchemas());
    int schemaSize = schemas.size();

    List<Path> outputs = new ArrayList<>();
    for (int scmCnt = 0; scmCnt < schemaSize; scmCnt++) {

      SchemaDef schema = schemas.get(scmCnt);
      log.info(
          "Schema count={}/{}, name={}",
          scmCnt + 1,
          schemaSize,
          Optional.ofNullable(schema.getName()).orElse("UNKNOWN"));

      GeneratedValueStore store = new GeneratedValueStore(config);

      List<TableDef> tables =
          schema.getTables().stream().sorted(comparator::compare).collect(Collectors.toList());
      int tableSize = tables.size();

      for (int tblCnt = 0; tblCnt < tableSize; tblCnt++) {

        TableDef table = tables.get(tblCnt);
        Integer rowCount = config.getRowCount(table);

        log.info(
            "Generating csv: count={}/{}, table={}, rowCnt={}",
            tblCnt + 1,
            tableSize,
            table.getFullyQualifiedName(),
            rowCount);

        List<Path> outPaths = write(table, rowCount, output, store);

        for (Path outPath : outPaths) {
          log.info(
              "Generated csv: table={}, byteSize={}, path={}",
              table.getFullyQualifiedName(),
              outPath.toFile().length(),
              outPath.toAbsolutePath());
        }

        outputs.addAll(outPaths);
      }
    }

    return outputs;
  }

  public List<Path> write(
      TableDef table, Integer rowCount, List<Path> out, GeneratedValueStore store)
      throws IOException {

    String schemaName = table.getSchemaName().orElse("UNKNOWN");
    String tableName = table.getName();
    String csvName = String.join(".", schemaName, tableName, "csv");

    return generate(table, rowCount, out, csvName, store);
  }

  public List<Path> generate(
      TableDef tableDef,
      Integer rowCount,
      List<Path> outDir,
      String fileName,
      GeneratedValueStore store)
      throws IOException {

    List<ColumnDef> cols = tableDef.getColumns();
    List<ColumnDef> pks =
        cols.stream().filter(ColumnDef::isPrimaryKey).collect(Collectors.toList());
    List<Object> header = cols.stream().map(ColumnDef::getName).collect(Collectors.toList());

    List<Path> outFiles = new ArrayList<>();

    try (DataWriter writer = DataWriter.build(outDir, fileName)) {
      writer.writeAppend(header);

      for (int row = 1; row <= rowCount; row++) {

        Optional<RandomValueRow> generatedValueRow = store.generateRow(cols, pks, row);

        if (generatedValueRow.isPresent()) {
          List<Object> lineValues = generatedValueRow.get().getLineValues();
          writer.writeAppend(lineValues);
        }
      }

      outFiles.addAll(writer.getFiles());
    }

    //    store.clearGeneratedRowsCache();

    return outFiles;
  }
}
