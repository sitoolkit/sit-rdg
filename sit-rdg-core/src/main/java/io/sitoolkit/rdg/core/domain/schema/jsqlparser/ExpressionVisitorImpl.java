package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.expression.ExpressionVisitorAdapter;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.InExpression;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.statement.select.SubSelect;

@Slf4j
public class ExpressionVisitorImpl extends ExpressionVisitorAdapter {

  SchemaInfoStore store;
  FromItemVisitorImpl fromItemVisitor;
  SelectVisitorImpl selectVisitor;

  List<RelationDef> relations;

  private List<ColumnPair> andPairs = new ArrayList<>();
  private boolean inAndExpr;

  public ExpressionVisitorImpl(SchemaInfoStore store, FromItemVisitorImpl fromItemVisitor) {
    this.store = store;
    this.fromItemVisitor = fromItemVisitor;
    this.selectVisitor = fromItemVisitor.selectVisitor;
    this.relations = new ArrayList<>();
  }

  public void visit(AndExpression expr) {
    inAndExpr = true;
    super.visitBinaryExpression(expr);
    if (!andPairs.isEmpty()) {
      store.addRelations(andPairs.stream().map(RelationDef::new).collect(Collectors.toList()));
      andPairs.clear();
    }
    inAndExpr = false;
  }

  @Override
  public void visit(InExpression expr) {
    if (Objects.nonNull(expr.getLeftExpression())) {
      expr.getLeftExpression().accept(this);
    }
    if (Objects.nonNull(expr.getLeftItemsList())) {
      expr.getLeftItemsList().accept(this);
    }
    if (Objects.nonNull(expr.getRightItemsList())) {
      expr.getRightItemsList().accept(this);
    }
  }

  @Override
  public void visit(EqualsTo expr) {

    try {

      Column leftColumn = (Column) expr.getLeftExpression();
      Optional<ColumnDef> leftColumnDef = store.getColumnDef(leftColumn);

      Column rightColumn = (Column) expr.getRightExpression();
      Optional<ColumnDef> rightColumnDef = store.getColumnDef(rightColumn);

      if (leftColumnDef.isPresent() && rightColumnDef.isPresent()) {

        ColumnDef left = ColumnDef.shallowCopyExcludeRelations(leftColumnDef.get());
        ColumnDef right = ColumnDef.shallowCopyExcludeRelations(rightColumnDef.get());

        if (left.equals(right)) {
          log.info("Skipped equal columns because same:[ left:{}, right:{} ]", left, right);
          return;
        }

        log.info("Found equal columns:[ left:{}, right:{} ]", left, right);

        if (inAndExpr) {
          ColumnPair pair = new ColumnPair(left, right);
          andPairs.add(pair);
        } else {
          store.addRelationalColumns(left, right);
        }

      } else {
        log.info(
            "Not found equal columns:[ left:{}, right:{} ]",
            leftColumn.getFullyQualifiedName(),
            rightColumn.getFullyQualifiedName());
      }

    } catch (ClassCastException e) {
      log.warn(
          "Failed casting columns:[ left:{}, right:{} ]",
          expr.getLeftExpression(),
          expr.getRightExpression());
    }
  }

  public void visit(SubSelect subSelect) {
    subSelect.getSelectBody().accept(selectVisitor);
  }
}
