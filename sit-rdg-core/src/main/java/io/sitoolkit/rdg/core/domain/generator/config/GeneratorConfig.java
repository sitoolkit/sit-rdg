package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sitoolkit.rdg.core.domain.generator.config.ColumnConfig.InheritanceType;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;

@Getter
public class GeneratorConfig {

  @JsonProperty("scale")
  private String scaleStr;

  @JsonProperty private boolean listedOnly = false;

  @JsonProperty private Long defaultRowCount;

  @JsonProperty private List<ColumnConfig> commonColumns = new ArrayList<>();

  @JsonManagedReference @JsonProperty
  private List<SchemaConfig> schemaConfigs = Collections.emptyList();

  @Getter(lazy = true)
  private final Map<String, ColumnConfig> commonColumnMap =
      commonColumns.stream().collect(Collectors.toMap(ColumnConfig::getName, s -> s));

  @JsonIgnore
  @Getter(lazy = true)
  private final Scale scale = Scale.parse(scaleStr);

  @JsonIgnore
  @Getter(lazy = true)
  private final Map<String, TableConfig> tableMap =
      schemaConfigs.stream()
          .flatMap(s -> s.getTableConfigs().stream())
          .collect(Collectors.toMap(TableConfig::getFullyQualifiedName, t -> t));

  @Getter(lazy = true)
  @JsonIgnore
  private final Map<String, ValueGenerator> valueGeneratorMap = initValueGenMap();

  @Getter(lazy = true)
  @JsonIgnore
  private final Map<String, ColumnConfig> columnMap =
      schemaConfigs.stream()
          .map(SchemaConfig::getTableConfigs)
          .flatMap(Collection::stream)
          .map(TableConfig::getColumnConfigs)
          .flatMap(Collection::stream)
          .collect(Collectors.toMap(ColumnConfig::getFullyQualifiedName, c -> c));

  public Long getDefaultRowCount() {
    if (Objects.isNull(defaultRowCount)) {
      defaultRowCount = Long.valueOf(5);
    }
    return defaultRowCount;
  }

  public long getRowCount(TableDef tableDef) {
    TableConfig tableConfig = getTableMap().get(tableDef.getFullyQualifiedName());
    long rowCount = tableConfig == null ? getDefaultRowCount() : tableConfig.getRowCount();
    return getScale().apply(rowCount);
  }

  public Optional<ValueGenerator> findValueGenerator(ColumnDef column) {
    return Optional.ofNullable(getValueGeneratorMap().get(column.getFullyQualifiedName()));
  }

  private Map<String, ValueGenerator> initValueGenMap() {
    return schemaConfigs.stream()
        .flatMap(s -> s.getTableConfigs().stream())
        .flatMap(t -> t.getColumnConfigs().stream())
        .collect(Collectors.toMap(ColumnConfig::getFullyQualifiedName, ColumnConfig::getSpec));
  }

  public Optional<RelationConfig> findRelationConfig(List<ColumnDef> subColumns) {
    List<String> columnNames =
        subColumns.stream().map(ColumnDef::getFullyQualifiedName).collect(Collectors.toList());

    for (SchemaConfig sconfig : schemaConfigs) {
      for (RelationConfig rconfig : sconfig.getRelationConfigs()) {
        if (columnNames.equals(rconfig.getSubColumns())) {
          return Optional.of(rconfig);
        }
      }
    }

    return Optional.empty();
  }

  public InheritanceType findColumnInheritanceType(String columnFqn) {
    ColumnConfig config = getColumnMap().get(columnFqn);
    return config == null ? InheritanceType.STORE : config.getInheritanceType();
  }

  public List<TableDef> filter(List<TableDef> tables) {
    if (listedOnly) {
      return tables.stream().filter(this::contains).collect(Collectors.toList());
    }

    return tables;
  }

  public boolean contains(TableDef table) {
    return getTableMap().containsKey(table.getFullyQualifiedName());
  }
}
