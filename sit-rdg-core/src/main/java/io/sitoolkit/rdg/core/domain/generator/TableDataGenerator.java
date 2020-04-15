package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TableDataGenerator {

  protected final TableDef table;

  protected List<RelationDataGenerator> generators = new ArrayList<>();

  protected final GeneratorConfig config;

  public List<Object> generateLine() {
    RowData rowData = generate();
    return rowData.toList(table.getColumns());
  }

  public abstract RowData generate();

  public void add(RelationDataGenerator generator) {
    generators.add(generator);
  }

  public String getTableName() {
    return table.getFullyQualifiedName();
  }

  public int getRequiredRowCount() {
    return config.getRowCount(table);
  }

  public List<Object> getHeader() {
    return table.getColumns().stream().map(ColumnDef::getName).collect(Collectors.toList());
  }
}
