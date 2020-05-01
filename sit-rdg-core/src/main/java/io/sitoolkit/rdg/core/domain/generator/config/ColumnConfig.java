package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import io.sitoolkit.rdg.core.domain.value.RandomGenerator;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import lombok.Getter;

@Getter
public class ColumnConfig {

  @JsonBackReference private TableConfig tableConfig;

  @JsonProperty("columnName")
  private String name;

  @JsonDeserialize(using = ValueGeneratorSpecDeserializer.class)
  private ValueGenerator spec = new RandomGenerator();

  @Getter(lazy = true)
  private final String fullyQualifiedName = initFqn();

  private String initFqn() {

    spec.initialize(this);

    return String.join(".", tableConfig.getName(), name);
  }
}
