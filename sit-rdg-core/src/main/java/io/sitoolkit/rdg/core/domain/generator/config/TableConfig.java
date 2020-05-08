package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class TableConfig {

  @JsonBackReference private SchemaConfig schemaConfig;

  @JsonProperty("tableName")
  private String name;

  @JsonProperty private Long rowCount;

  @JsonManagedReference @JsonProperty
  private List<ColumnConfig> columnConfigs = Collections.emptyList();

  @JsonManagedReference @JsonProperty("skipUniqueCheck")
  private List<UniqueConstraintConfig> skipUniqueCheckConfigs = Collections.emptyList();

  @Getter(lazy = true)
  private final String fullyQualifiedName =
      StringUtils.isEmpty(schemaConfig.getName())
          ? getName()
          : schemaConfig.getName() + "." + getName();

  public Long getRowCount() {
    if (Objects.isNull(rowCount)) {
      rowCount = schemaConfig.getRowCount();
    }
    return rowCount;
  }
}
