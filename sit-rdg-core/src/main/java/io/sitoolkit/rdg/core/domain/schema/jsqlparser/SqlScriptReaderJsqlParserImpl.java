package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.SqlScriptReader;
import java.nio.file.Path;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.Statements;

/** SqlScriptReaderJsqlParserImpl */
@Slf4j
public class SqlScriptReaderJsqlParserImpl implements SqlScriptReader {

  private SchemaInfoStore store = new SchemaInfoStore();

  @Override
  public void read(Path sqlScriptFile) {
    log.info("Read: {}", sqlScriptFile.toAbsolutePath());
    List<Statements> statementsList = Converter.sqls2statements(List.of(sqlScriptFile));

    StatementVisitorImpl statementVisitor = new StatementVisitorImpl(store);
    statementVisitor.visit(statementsList);
  }

  @Override
  public SchemaInfo getSchemaInfo() {

    return new SchemaInfo(store.getSchemas());
  }
}
