package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class TableDataGenerator {

  @Getter(AccessLevel.PROTECTED)
  private final TableDef table;

  @Getter private List<RelationDataGenerator> generators = new ArrayList<>();

  @Getter(AccessLevel.PROTECTED)
  private final GeneratorConfig config;

  @Getter(AccessLevel.PROTECTED)
  private UniqueDataStore uniqueDataStore = new UniqueDataStore();

  public List<Object> generateLine() {
    RowData rowData = generate();
    return rowData.toList(table.getColumns());
  }

  public abstract RowData generate();

  public void add(RelationDataGenerator generator) {
    generator.setUniqueDataStore(uniqueDataStore);
    generators.add(generator);
  }

  public String getTableName() {
    return table.getFullyQualifiedName();
  }

  public long getRequiredRowCount() {
    return config.getRowCount(table);
  }

  public List<Object> getHeader() {
    return table.getColumns().stream().map(ColumnDef::getName).collect(Collectors.toList());
  }
}
