package io.sitoolkit.rdg.core.domain.generator.config;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class TableConfig implements Comparable<TableConfig> {

  @JsonBackReference private SchemaConfig schemaConfig;

  @JsonProperty("tableName")
  private String name;

  @JsonProperty("priorityRank")
  private Integer priorityRank;

  @JsonProperty("rowCount")
  private Integer rowCount;

  @JsonProperty("requiredValueCount")
  private Integer requiredValueCount;

  @JsonManagedReference
  @JsonProperty("columnConfigs")
  private List<ColumnConfig> columnConfigs = Collections.emptyList();;

  public Integer getRowCount() {
    if (Objects.isNull(rowCount)) {
      rowCount = schemaConfig.getRowCount();
    }
    return rowCount;
  }

  public Integer getRequiredValueCount() {
    if (Objects.isNull(requiredValueCount)) {
      requiredValueCount = schemaConfig.getRequiredValueCount();
    }
    return requiredValueCount;
  }

  @JsonIgnore
  public String getFullQualifiedName() {
    if (StringUtils.isEmpty(schemaConfig.getName())) {
      return name;
    }
    return String.join(".", schemaConfig.getName(), name);
  }

  @Override
  public int compareTo(TableConfig o) {
    int result = (int) (priorityRank - o.priorityRank);
    if (result == 0) {
      return name.compareTo(o.name);
    }
    return result;
  }
}
