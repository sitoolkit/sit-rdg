package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.CsvData;
import io.sitoolkit.rdg.core.infrastructure.CsvUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataRelationChecker {

  public CheckResult checkDir(Path inDir, Path outDir) {
    return checkDirs(inDir, List.of(outDir));
  }

  public CheckResult checkDirs(Path inDir, List<Path> outDirs) {

    List<Path> dataFiles =
        outDirs.stream()
            .map(this::findCsvFiles)
            .flatMap(Collection::stream)
            .collect(Collectors.toList());

    return checkFiles(inDir, dataFiles);
  }

  List<Path> findCsvFiles(Path dir) {
    List<Path> csvFiles = new ArrayList<>();

    try (Stream<Path> files = Files.walk(dir, FileVisitOption.FOLLOW_LINKS)) {
      files.filter(CsvUtils::isCsvFile).forEach(csvFiles::add);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return csvFiles;
  }

  public CheckResult checkFiles(Path inDir, List<Path> dataFiles) {

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
        checkResult.okRelations.add(relation);
      } else {
        log.info("NG " + relation);
        checkResult.ngRelations.add(relation);
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

    List<RelationDef> ngRelations = new ArrayList<>();

    List<RelationDef> okRelations = new ArrayList<>();

    Path addFile(Path file) {
      chekedFileNames.add(file.getFileName().toString());
      return file;
    }

    public boolean hasError() {
      return !errorList.isEmpty();
    }

    public String getErrorMessage() {
      List<String> message = new ArrayList<>();

      Set<TableDef> okTables = rels2tab(okRelations);
      Set<TableDef> ngTables = rels2tab(ngRelations);

      message.add("OK: " + okTables.size() + " tables, " + okRelations.size());
      message.add("NG: " + ngTables.size() + " tables, " + ngRelations.size());

      message.add("");

      message.add("OK Tables: " + tabs2str(okTables));
      message.add("OK Relations: " + rels2str(okRelations));

      message.add("");

      message.add("NG Tables: " + tabs2str(okTables));
      message.add("NG Relations: " + rels2str(ngRelations));

      return message.stream().collect(Collectors.joining(System.lineSeparator()));
    }

    Set<TableDef> rels2tab(List<RelationDef> rels) {
      return rels.stream()
          .map(RelationDef::getTables)
          .flatMap(Collection::stream)
          .collect(Collectors.toSet());
    }

    String tabs2str(Collection<TableDef> tables) {
      return tables.stream().map(TableDef::getName).collect(Collectors.joining(","));
    }

    String rels2str(Collection<RelationDef> relations) {
      return relations.stream()
          .map(RelationDef::toString)
          .collect(Collectors.joining(System.lineSeparator()));
    }
  }
}
