package io.sitoolkit.rdg.core.domain.generator.config;

import java.util.Objects;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;

@Getter
public class ColumnConfig {

  @JsonBackReference private TableConfig tableConfig;

  @JsonProperty("columnName")
  private String name;

  @JsonProperty("requiredValueCount")
  private Integer requiredValueCount;

  public Integer getRequiredValueCount() {
    if (Objects.isNull(requiredValueCount)) {
      requiredValueCount = tableConfig.getRequiredValueCount();
    }
    return requiredValueCount;
  }

  @JsonIgnore
  public String getFullQualifiedName() {
    if (StringUtils.isEmpty(tableConfig.getName())) {
      return name;
    }
    return String.join(".", tableConfig.getName(), name);
  }
}
