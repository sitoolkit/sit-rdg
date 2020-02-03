package io.sitoolkit.rdg.core.application;

import java.io.IOException;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import io.sitoolkit.rdg.core.domain.Converter;
import io.sitoolkit.rdg.core.domain.schema.SchemaDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.visitor.SchemaInfoStore;
import io.sitoolkit.rdg.core.domain.visitor.StatementVisitorImpl;
import io.sitoolkit.rdg.core.infrastructure.JsonUtils;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statements;

@Slf4j
public class SchemaAnalyzer {

  public Path analyze(Path input) throws IOException {

    SchemaInfo schemaInfo = read(input);

    String json = JsonUtils.object2json(schemaInfo);

    return Files.writeString(input.resolve("schema.json"), json);
  }

  public SchemaInfo read(Path input) throws IOException {
    List<Path> sqls =
        Files.walk(input, FileVisitOption.FOLLOW_LINKS)
            .filter(p -> StringUtils.endsWith(p.getFileName().toString(), ".sql"))
            .peek(sql -> log.info("Read sqls:{}", sql.toString()))
            .collect(Collectors.toList());

    List<Statements> statementsList = Converter.sqls2statements(sqls);

    SchemaInfoStore store = new SchemaInfoStore();

    StatementVisitorImpl statementVisitor = new StatementVisitorImpl(store);
    statementVisitor.visit(statementsList);

    Set<SchemaDef> schemas = store.getSchemas();
    SchemaInfo schemaInfo = new SchemaInfo(schemas);

    return schemaInfo;
  }
}
