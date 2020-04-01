package io.sitoolkit.rdg.core.domain.schema.jsqlparser;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.ConstraintAttribute;
import io.sitoolkit.rdg.core.domain.schema.DataType;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.SqlUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statements;
import net.sf.jsqlparser.statement.create.table.ColumnDefinition;
import net.sf.jsqlparser.statement.create.table.CreateTable;

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Converter {

  public static List<Statements> sqls2statements(List<Path> sqls) {

    List<Statements> statements =
        sqls.stream()
            .map(Converter::sql2statements)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toList());

    return statements;
  }

  public static TableDef createTable2tableDef(CreateTable createTable) {

    TableDef tableDef = table2TableDef(createTable.getTable());

    List<ColumnDef> columns =
        createTable.getColumnDefinitions().stream()
            .map(Converter::columnDefinition2columnDef)
            .peek(
                c -> {
                  List<ConstraintAttribute> constraints =
                      ConstraintAttribute.matchedConstraints(createTable.getIndexes(), c);
                  c.setConstraints(constraints);
                  c.setTable(tableDef);
                })
            .collect(Collectors.toList());

    tableDef.setColumns(columns);

    return tableDef;
  }

  public static TableDef table2TableDef(Table table) {

    String schemaName = table.getSchemaName();
    String name = table.getName();
    String alias = name;
    if (Objects.nonNull(table.getAlias())) {
      alias = table.getAlias().getName();
    }

    TableDef tableDef =
        TableDef.builder()
            .schemaName(schemaName)
            .alias(alias)
            .name(name)
            .columns(new ArrayList<>())
            .build();

    return tableDef;
  }

  public static ColumnDef columnDefinition2columnDef(ColumnDefinition columnDefinition) {

    String name = columnDefinition.getColumnName();
    DataType dataType = DataType.parse(columnDefinition.getColDataType().getDataType());
    List<String> args = columnDefinition.getColDataType().getArgumentsStringList();

    ColumnDef columnDef =
        ColumnDef.builder()
            .name(name)
            .dataType(dataType)
            .args(args)
            .constraints(new ArrayList<>())
            .build();

    return columnDef;
  }

  public static Optional<Statements> sql2statements(Path sql) {
    try {
      return sql2statements(SqlUtils.readSql(sql));
    } catch (IOException e) {
      log.warn(e.getMessage());
    }
    return Optional.empty();
  }

  public static Optional<Statements> sql2statements(String sql) {
    try {
      return Optional.of(CCJSqlParserUtil.parseStatements(sql));
    } catch (JSQLParserException e) {
      e.printStackTrace();
      log.warn(sql);
    }
    return Optional.empty();
  }
}
