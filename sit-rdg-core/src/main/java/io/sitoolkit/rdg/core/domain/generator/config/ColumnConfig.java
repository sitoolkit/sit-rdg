package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.sitoolkit.rdg.core.domain.value.RandomGenerator;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import java.util.Objects;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
public class ColumnConfig {

  @JsonBackReference private TableConfig tableConfig;

  @JsonProperty("columnName")
  private String name;

  @JsonDeserialize(using = ValueGeneratorSpecDeserializer.class)
  private ValueGenerator spec = new RandomGenerator();

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
