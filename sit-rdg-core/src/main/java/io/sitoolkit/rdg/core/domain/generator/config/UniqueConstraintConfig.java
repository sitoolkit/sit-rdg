package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString(exclude = {"table"})
public class UniqueConstraintConfig {

  @JsonBackReference
  private TableConfig table;

  @JsonProperty("columns")
  private List<String> columnNames = new ArrayList<>();
}
