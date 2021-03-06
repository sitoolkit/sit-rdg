package io.sitoolkit.rdg.core.application;

import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.SqlScriptReader;
import io.sitoolkit.rdg.core.domain.schema.jsqlparser.SqlScriptReaderJsqlParserImpl;
import io.sitoolkit.rdg.core.infrastructure.SqlFileUtils;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SchemaAnalyzer {

  public Path analyze(Path inDirOrFile) {

    SchemaInfo schemaInfo = read(inDirOrFile);

    Path outDir = inDirOrFile.toFile().isFile() ? inDirOrFile.getParent() : inDirOrFile;

    return schemaInfo.write(outDir);
  }

  public SchemaInfo read(Path inDir) {

    SqlScriptReader scriptReader = new SqlScriptReaderJsqlParserImpl();

    try (Stream<Path> inFiles = Files.walk(inDir, FileVisitOption.FOLLOW_LINKS)) {
      inFiles
          .filter(SqlFileUtils::isSqlFile)
          .sorted()
          .map(SqlFileUtils::readSql)
          .forEach(scriptReader::read);
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return scriptReader.getSchemaInfo();
  }
}
