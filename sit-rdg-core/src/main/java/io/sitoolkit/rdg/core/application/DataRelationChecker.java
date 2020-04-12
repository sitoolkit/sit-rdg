package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.infrastructure.CsvData;
import io.sitoolkit.rdg.core.infrastructure.CsvUtils;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataRelationChecker {

  public CheckResult check(Path inDir, Path outDir) {

    List<Path> dataFiles = new ArrayList<>();

    try (Stream<Path> files = Files.walk(outDir, FileVisitOption.FOLLOW_LINKS)) {
      files
          .filter(CsvUtils::isCsvFile)
          .map(Path::toFile)
          .sorted(Comparator.comparing(File::lastModified))
          .map(File::toPath)
          .forEach(dataFiles::add);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return check(inDir, dataFiles);
  }

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
    List<String> mainCols = cols2names(relation.getLeftColumns());
    List<String> subCols = cols2names(relation.getRightColumns());

    return mainData.containsAll(mainCols, subData, subCols);
  }

  List<String> cols2names(List<ColumnDef> columns) {
    return columns.stream().map(ColumnDef::getName).collect(Collectors.toList());
  }

  public static class CheckResult {
    @Getter List<String> chekedFileNames = new ArrayList<>();

    @Getter List<String> errorList = new ArrayList<>();

    Path addFile(Path file) {
      chekedFileNames.add(file.getFileName().toString());
      return file;
    }
  }
}
