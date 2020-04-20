package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.generator.DataGeneratorFactory;
import io.sitoolkit.rdg.core.domain.generator.TableDataGenerator;
import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfigReader;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.infrastructure.DataWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DataGeneratorOptimizedImpl implements DataGenerator {

  GeneratorConfigReader reader = new GeneratorConfigReader();

  NumberFormat f = NumberFormat.getNumberInstance();

  public List<Path> generate(Path input, List<Path> outDirs) {
    SchemaInfo schemaInfo = SchemaInfo.read(input);

    GeneratorConfig config = reader.read(input);

    List<TableDataGenerator> generators =
        DataGeneratorFactory.build(schemaInfo.getAllTables(), config);

    log.info(
        "Generating order : {}",
        generators.stream().map(TableDataGenerator::getTableName).collect(Collectors.joining(",")));

    return generate(generators, outDirs);
  }

  List<Path> generate(List<TableDataGenerator> generators, List<Path> outDirs) {

    List<Path> outFiles = new ArrayList<>();
    int tableCount = generators.size();
    int generatedTableCount = 1;

    for (TableDataGenerator generator : generators) {

      long rowCount = generator.getRequiredRowCount();

      log.info(
          "Start generating {} rows to {} {}/{}",
          f.format(rowCount),
          generator.getTableName(),
          generatedTableCount,
          tableCount);

      try (DataWriter writer = DataWriter.build(outDirs, generator.getTableName() + ".csv")) {

        writer.writeAppend(generator.getHeader());

        for (long i = 0; i < rowCount; i++) {
          writer.writeAppend(generator.generateLine());

          if (i % 10000 == 0 && i > 0) {
            log.info("{}/{}", f.format(i), f.format(rowCount));
          }
        }

        generator.end();

        outFiles.addAll(writer.getFiles());

      } catch (IOException e) {
        throw new UncheckedIOException(e);
      }

      generatedTableCount++;
    }

    return outFiles;
  }

  void writeOrderFile(List<Path> outFiles, Path outDir) {
    String orderString =
        outFiles.stream()
            .map(Path::getFileName)
            .map(Path::toString)
            .map(fileName -> fileName.replace(".csv", ""))
            .collect(Collectors.joining(System.lineSeparator()));
    Path orderFile = outDir.resolve("order.txt");
    try {
      Files.writeString(orderFile, orderString);
      log.info("Write: {}", orderFile);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }
}
