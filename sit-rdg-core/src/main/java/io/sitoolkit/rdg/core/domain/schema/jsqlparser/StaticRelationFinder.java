package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.create.table.CreateTable;

/** StaticRelationFinder */
@AllArgsConstructor
@Slf4j
public class StaticRelationFinder extends StatementVisitorAdapter {

  private SchemaInfoStore store;

  @Override
  public void visit(CreateTable createTable) {
    log.debug("Visit: {}", createTable);
    store.addTable(Converter.createTable2tableDef(createTable));
  }
}
