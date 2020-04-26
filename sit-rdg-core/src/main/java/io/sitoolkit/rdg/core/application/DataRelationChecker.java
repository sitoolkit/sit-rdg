package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.check.CheckResult;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import io.sitoolkit.rdg.core.infrastructure.CsvData;
import io.sitoolkit.rdg.core.infrastructure.CsvUtils;
import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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
        .map(Path::toFile)
        .sorted(Comparator.comparing(File::lastModified))
        .map(File::toPath)
        .peek(checkResult::addFile)
        .map(CsvUtils::read)
        .forEach(csvData -> csvDataMap.put(csvData.getFileName().replace(".csv", ""), csvData));

    SchemaInfo schemaInfo = SchemaInfo.read(inDir);

    for (RelationDef relation : schemaInfo.getAllRelations()) {
      boolean result =
          check(
              relation,
              csvDataMap.get(relation.getMainTable().getName()),
              csvDataMap.get(relation.getSubTable().getName()));

      if (result) {
        log.info("OK " + relation);
        checkResult.getOkRelations().add(relation);
      } else {
        log.info("NG " + relation);
        checkResult.getNgRelations().add(relation);
      }
    }

    for (TableDef table : schemaInfo.getAllTables()) {
      for (UniqueConstraintDef unique : table.getUniqueConstraints()) {
        boolean result = csvDataMap.get(table.getName()).isUnique(unique.getColumnNames());

        if (result) {
          log.info("OK " + unique);
          checkResult.getOkUniques().add(unique);
        } else {
          log.info("NG " + unique);
          checkResult.getNgUniques().add(unique);
        }
      }
    }

    return checkResult;
  }

  boolean check(RelationDef relation, CsvData mainData, CsvData subData) {
    List<String> mainCols = cols2names(relation.getMainColumns());
    List<String> subCols = cols2names(relation.getSubColumns());

    return mainData.containsAll(mainCols, subData, subCols);
  }

  List<String> cols2names(List<ColumnDef> columns) {
    return columns.stream().map(ColumnDef::getName).collect(Collectors.toList());
  }
}
