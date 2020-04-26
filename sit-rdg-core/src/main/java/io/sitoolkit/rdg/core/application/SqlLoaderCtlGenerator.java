package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.ResourceUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SqlLoaderCtlGenerator {

  public void generate(Path inDir, Path outDir) {
    SchemaInfo schemaInfo = SchemaInfo.read(inDir);

    if (!outDir.toFile().exists()) {
      outDir.toFile().mkdirs();
      log.info("Make directory: {}", outDir.toAbsolutePath().normalize());
    }

    for (TableDef table : schemaInfo.getAllTables()) {
      String ctl = table2ctl(table);

      Path outFile = outDir.resolve(table.getFullyQualifiedName() + ".ctl");

      try {
        Files.writeString(outFile, ctl);
        log.info("Write: {}", outFile.toAbsolutePath().normalize());
      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }
    }
  }

  String table2ctl(TableDef table) {
    Map<String, Object> params = new HashMap<>();

    params.put("inFile", table.getFullyQualifiedName() + ".csv");

    String columnNames = buildColumnNames(table);

    params.put("columnNames", columnNames);

    params.put("tableName", table.getName());
    params.put("badFile", table.getName() + ".bad");

    String template = ResourceUtils.res2str("template.ctl");

    return replace(template, params);
  }

  String buildColumnNames(TableDef table) {
    StringBuilder sb = new StringBuilder();

    for (ColumnDef column : table.getColumns()) {
      if (sb.length() > 0) {
        sb.append(",\n");
      }

      sb.append(column.getName());
      sb.append(" ");

      switch (column.getDataType().getName()) {
        case CHAR:
        case VARCHAR:
          if (column.getDataType().getSize() > 255) {
            sb.append("CHAR(" + column.getDataType().getSize() + ")");
          }
          break;
        case DATE:
          sb.append("DATE \"YYYY-MM-DD\"");
          break;
        case TIMESTAMP:
          sb.append("TIMESTAMP \"YYYY-MM-DD HH24:MI:SS.FF3\"");
          break;
        default:
          break;
      }
    }

    return sb.toString();
  }

  String replace(String template, Map<String, Object> params) {

    for (Entry<String, Object> param : params.entrySet()) {
      template = replace(template, param.getKey(), param.getValue());
    }

    return template;
  }

  String replace(String template, String key, Object value) {
    return template.replace("${" + key + "}", value.toString());
  }
}
