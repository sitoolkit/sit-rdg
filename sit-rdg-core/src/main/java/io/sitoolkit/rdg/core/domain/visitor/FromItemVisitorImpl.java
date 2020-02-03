package io.sitoolkit.rdg.core.domain.visitor;

import java.util.Objects;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.select.FromItemVisitorAdapter;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.SubJoin;
import net.sf.jsqlparser.statement.select.SubSelect;

public class FromItemVisitorImpl extends FromItemVisitorAdapter {

  SchemaInfoStore store;
  SelectVisitorImpl selectVisitor;
  ExpressionVisitorImpl expressionVisitor;

  public FromItemVisitorImpl(SchemaInfoStore store, SelectVisitorImpl selectVisitor) {
    this.store = store;
    this.selectVisitor = selectVisitor;
    this.expressionVisitor = new ExpressionVisitorImpl(store, this);
  }

  @Override
  public void visit(Table table) {
    if (Objects.nonNull(table.getAlias())) {
      store.putAlias(table.getAlias().getName(), table.getName());
    }
  }

  @Override
  public void visit(SubSelect subSelect) {
    subSelect.getSelectBody().accept(selectVisitor);
  }

  @Override
  public void visit(SubJoin subjoin) {
    visit(subjoin.getJoin());
  }

  public void visit(Join join) {
    if (Objects.nonNull(join)) {
      join.getRightItem().accept(this);
      if (Objects.nonNull(join.getOnExpression())) {
        join.getOnExpression().accept(expressionVisitor);
      }
    }
  }
}
