package io.sitoolkit.rdg.core.domain.generator.config;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import lombok.Getter;

@Getter
public class GeneratorConfig {

  @JsonProperty("defaultRowCount")
  private Integer defaultRowCount;

  @JsonProperty("defaultRequiredValueCount")
  private Integer defaultRequiredValueCount;

  @JsonManagedReference
  @JsonProperty("schemaConfigs")
  private List<SchemaConfig> schemaConfigs = Collections.emptyList();

  public Integer getDefaultRowCount() {
    if (Objects.isNull(defaultRowCount)) {
      defaultRowCount = Integer.valueOf(1000);
    }
    return defaultRowCount;
  }

  public Integer getDefaultRequiredValueCount() {
    if (Objects.isNull(defaultRequiredValueCount)) {
      defaultRequiredValueCount = Integer.valueOf(1000);
    }
    return defaultRequiredValueCount;
  }

  @JsonIgnore
  @Getter(lazy = true)
  private final Map<String, Integer> rowCountMap =
      schemaConfigs
          .stream()
          .flatMap(s -> s.getTableConfigs().stream())
          .collect(Collectors.toMap(t -> t.getFullQualifiedName(), t -> t.getRowCount()));

  @JsonIgnore
  @Getter(lazy = true)
  private final Map<String, Integer> requiredValueCountMap =
      schemaConfigs
          .stream()
          .flatMap(s -> s.getTableConfigs().stream())
          .flatMap(t -> t.getColumnConfigs().stream())
          .collect(Collectors.toMap(c -> c.getFullQualifiedName(), c -> c.getRequiredValueCount()));

  @JsonIgnore
  public Integer getRowCount(TableDef tableDef) {
    return getRowCountMap().getOrDefault(tableDef.getFullyQualifiedName(), getDefaultRowCount());
  }

  @JsonIgnore
  public Integer getRequiredValueCount(ColumnDef col) {

    Integer requiredValueCount =
        getRequiredValueCountMap()
            .getOrDefault(col.getFullyQualifiedName(), getDefaultRequiredValueCount());

    return requiredValueCount;
  }
  //  @JsonIgnore
  //  public Integer getRequiredValueCount(RelationDef relation) {
  //
  //    Integer requiredValueCount =
  //        relation
  //            .getDistinctColumns()
  //            .parallelStream()
  //            .map(ColumnDef::getFullyQualifiedName)
  //            .map(getRequiredValueCountMap()::get)
  //            .filter(Objects::nonNull)
  //            .findAny()
  //            .orElse(getDefaultRequiredValueCount());
  //
  //    return requiredValueCount;
  //  }
}
