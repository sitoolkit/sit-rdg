package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.SqlScriptReader;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.Statements;

/** SqlScriptReaderJsqlParserImpl */
@Slf4j
public class SqlScriptReaderJsqlParserImpl implements SqlScriptReader {

  @Getter private SchemaInfo schemaInfo = new SchemaInfo();

  @Override
  public void read(String sqlText) {

    StaticRelationFinder staticRelFinder = new StaticRelationFinder(schemaInfo);

    try {
      Statements stmts = CCJSqlParserUtil.parseStatements(sqlText);

      stmts.accept(staticRelFinder);

    } catch (JSQLParserException e) {
      log.error("Error on parsing", e);
    }
  }
}
