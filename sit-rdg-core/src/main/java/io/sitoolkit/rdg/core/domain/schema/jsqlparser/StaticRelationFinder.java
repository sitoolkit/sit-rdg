package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.StatementVisitorAdapter;
import net.sf.jsqlparser.statement.alter.Alter;
import net.sf.jsqlparser.statement.alter.AlterExpression;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.ForeignKeyIndex;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.create.table.NamedConstraint;
import org.apache.commons.lang3.StringUtils;

/** StaticRelationFinder */
@AllArgsConstructor
@Slf4j
public class StaticRelationFinder extends StatementVisitorAdapter {

  private SchemaInfo schemaInfo;

  @Override
  public void visit(CreateTable createTable) {
    log.debug("Visit: {}", createTable);
    schemaInfo.add(
        createTable.getTable().getSchemaName(), JsqlParserConverter.convert(createTable));

    analyzeSinglePk(createTable);

    if (createTable.getIndexes() == null) {
      return;
    }

    for (Index index : createTable.getIndexes()) {

      if (index instanceof ForeignKeyIndex) {
        analyzeFk(createTable.getTable(), (ForeignKeyIndex) index);
      } else if (index instanceof NamedConstraint) {
        analyzeNamedConstraint(createTable.getTable(), (NamedConstraint) index);
      }
    }
  }

  @Override
  public void visit(Alter alter) {
    log.debug("Visit: {}", alter);

    for (AlterExpression alterExp : alter.getAlterExpressions()) {
      if (alterExp.getIndex() instanceof ForeignKeyIndex) {
        analyzeFk(alter.getTable(), (ForeignKeyIndex) alterExp.getIndex());
      } else if (alterExp.getIndex() instanceof NamedConstraint) {
        analyzeNamedConstraint(alter.getTable(), (NamedConstraint) alterExp.getIndex());
      }

      List<String> ukColumns = alterExp.getUkColumns();
      if (ukColumns != null) {
        registerUnique(alter.getTable().getSchemaName(), alter.getTable().getName(), ukColumns);
      }
    }
  }

  public void analyzeFk(Table table, ForeignKeyIndex fk) {
    log.debug("Analyze: {}", fk);
    String schemaName = StringUtils.defaultString(table.getSchemaName());
    String schemaNamePart = StringUtils.isEmpty(schemaName) ? "" : schemaName + ".";

    Optional<TableDef> mainTableOpt = schemaInfo.findTable(schemaName, fk.getTable().getName());

    if (!mainTableOpt.isPresent()) {
      return;
    }

    String referencedTableName = fk.getTable().getName();
    String readingTableName = table.getName();
    List<ColumnPair> pairs = new ArrayList<>();

    for (int i = 0; i < fk.getColumnsNames().size(); i++) {
      String mainColumn =
          schemaNamePart + referencedTableName + "." + fk.getReferencedColumnNames().get(i);
      String subColumn = schemaNamePart + readingTableName + "." + fk.getColumnsNames().get(i);

      pairs.add(new ColumnPair(mainColumn, subColumn));
      // ColumnDef mainColumn =
      //     schemaInfo
      //         .findColumn(
      //             fk.getTable().getSchemaName(),
      //             referencedTableName,
      //             fk.getReferencedColumnNames().get(i))
      //         .orElseThrow();
      // ColumnDef subColumn =
      //     schemaInfo
      //         .findColumn(table.getSchemaName(), readingTableName, fk.getColumnsNames().get(i))
      //         .orElseThrow();

      // pairs.add(new ColumnPair(mainColumn, subColumn));
    }

    RelationDef relation = new RelationDef();
    relation.addAllPairs(pairs);

    schemaInfo.findByName(table.getSchemaName()).orElseThrow().getRelations().add(relation);
  }

  void analyzeNamedConstraint(Table table, NamedConstraint constraint) {
    log.debug("Analyze: {}", constraint);

    if (StringUtils.equalsAny(constraint.getType().toLowerCase(), "primary key", "unique")) {

      registerUnique(table.getSchemaName(), table.getName(), constraint.getColumnsNames());
    }
  }

  void analyzeSinglePk(CreateTable createTable) {
    for (ColumnDefinition colDef : createTable.getColumnDefinitions()) {

      if (colDef.getColumnSpecStrings() == null) {
        continue;
      }

      for (String spec : colDef.getColumnSpecStrings()) {
        if ("primary".equalsIgnoreCase(spec)) {

          registerUnique(
              createTable.getTable().getSchemaName(),
              createTable.getTable().getName(),
              List.of(colDef.getColumnName()));
        }
      }
    }
  }

  void registerUnique(String schemaName, String tableName, List<String> columnNames) {
    TableDef tableDef = schemaInfo.findTable(schemaName, tableName).orElseThrow();
    UniqueConstraintDef unique = new UniqueConstraintDef();
    unique.setTable(tableDef);
    unique.setColumnNames(columnNames);
    tableDef.getUniqueConstraints().add(unique);
  }
}
