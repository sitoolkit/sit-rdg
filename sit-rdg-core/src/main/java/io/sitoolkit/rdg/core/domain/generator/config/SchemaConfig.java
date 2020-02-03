package io.sitoolkit.rdg.core.domain.generator.config;

import java.util.Objects;
import java.util.SortedSet;
import java.util.TreeSet;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.EqualsAndHashCode;
import lombok.Getter;

@Getter
@EqualsAndHashCode
public class SchemaConfig {

  @JsonBackReference private GeneratorConfig setting;

  @JsonProperty("schemaName")
  private String name;

  @JsonProperty("rowCount")
  private Integer rowCount;

  @JsonProperty("requiredValueCount")
  private Integer requiredValueCount;

  @JsonManagedReference
  @JsonProperty("tableConfigs")
  private SortedSet<TableConfig> tableConfigs = new TreeSet<>();

  public Integer getRowCount() {
    if (Objects.isNull(rowCount)) {
      rowCount = setting.getDefaultRowCount();
    }
    return rowCount;
  }

  public Integer getRequiredValueCount() {
    if (Objects.isNull(requiredValueCount)) {
      requiredValueCount = setting.getDefaultRequiredValueCount();
    }
    return requiredValueCount;
  }
}
