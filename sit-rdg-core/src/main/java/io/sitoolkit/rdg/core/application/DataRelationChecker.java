package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.CsvData;
import io.sitoolkit.rdg.core.infrastructure.CsvUtils;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataRelationChecker {

  public CheckResult check(Path inDir, List<Path> dataFiles) {

    CheckResult checkResult = new CheckResult();

    Map<String, CsvData> csvDataMap = new HashMap<>();

    dataFiles.stream()
        .peek(checkResult::addFile)
        .map(CsvUtils::read)
        .forEach(csvData -> csvDataMap.put(csvData.getFileName().replace(".csv", ""), csvData));

    SchemaInfo schemaInfo = SchemaInfo.read(inDir);

    for (RelationDef relation : schemaInfo.getAllRelations()) {
      boolean result =
          check(
              relation,
              csvDataMap.get(relation.getLeftTable().getName()),
              csvDataMap.get(relation.getRightTable().getName()));

      if (result) {
        log.info("OK " + relation);
      } else {
        checkResult.errorList.add("NG " + relation);
      }
    }

    return checkResult;
  }

  boolean check(RelationDef relation, CsvData mainData, CsvData subData) {
    List<String> mainCols = tab2cols(relation.getLeftTable());
    List<String> subCols = tab2cols(relation.getRightTable());

    return mainData.containsAll(mainCols, subData, subCols);
  }

  List<String> tab2cols(TableDef table) {
    return table.getColumns().stream().map(ColumnDef::getName).collect(Collectors.toList());
  }

  static class CheckResult {
    @Getter List<String> chekedFileNames = new ArrayList<>();

    @Getter List<String> errorList = new ArrayList<>();

    Path addFile(Path file) {
      chekedFileNames.add(file.getFileName().toString());
      return file;
    }
  }
}
