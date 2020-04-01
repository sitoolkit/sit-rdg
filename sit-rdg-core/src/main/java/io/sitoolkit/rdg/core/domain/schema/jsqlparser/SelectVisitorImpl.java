package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import java.util.Objects;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.SelectVisitorAdapter;

public class SelectVisitorImpl extends SelectVisitorAdapter {

  SchemaInfoStore store;
  FromItemVisitorImpl fromItemsVisitor;
  ExpressionVisitorImpl expressionVisitor;

  public SelectVisitorImpl(SchemaInfoStore store) {
    this.store = store;
    this.fromItemsVisitor = new FromItemVisitorImpl(store, this);
    this.expressionVisitor = fromItemsVisitor.expressionVisitor;
  }

  @Override
  public void visit(PlainSelect plainSelect) {

    plainSelect.getFromItem().accept(fromItemsVisitor);

    if (Objects.nonNull(plainSelect.getJoins())) {
      plainSelect.getJoins().stream().forEach(fromItemsVisitor::visit);
    }

    if (Objects.nonNull(plainSelect.getWhere())) {
      plainSelect.getWhere().accept(expressionVisitor);
    }
  }
}
