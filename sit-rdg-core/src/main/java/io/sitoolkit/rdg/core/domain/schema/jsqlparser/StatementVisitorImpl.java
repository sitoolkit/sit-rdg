package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;

@Slf4j
public class StatementVisitorImpl extends StatementVisitorAdapter {

  SchemaInfoStore store;
  SelectVisitorImpl selectVisitor;

  private List<SelectBody> selects;

  public StatementVisitorImpl(SchemaInfoStore store) {
    this.store = store;
    this.selectVisitor = new SelectVisitorImpl(store);
    this.selects = new ArrayList<>();
  }

  public void visit(List<Statements> statementsList) {
    statementsList.forEach(this::visit);

    selects.stream().peek(s -> log.info("Read sql select:{}", s)).forEach(this::visit);

    store.mergeRelations();
  }

  public void visit(SelectBody selectBody) {
    selectBody.accept(selectVisitor);
  }

  @Override
  public void visit(Statements statements) {
    statements.getStatements().forEach(s -> s.accept(this));
  }

  @Override
  public void visit(Select select) {
    selects.add(select.getSelectBody());
  }

  @Override
  public void visit(CreateTable createTable) {
    log.info("Read sql createTable:{}", createTable.getTable().getFullyQualifiedName());
    store.addTable(Converter.createTable2tableDef(createTable));
  }
}
