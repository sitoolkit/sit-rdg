package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class ColumnDef {

  @JsonBackReference private TableDef table;

  @JsonProperty("columnName")
  private String name;

  @Getter(lazy = true)
  @JsonIgnore
  @EqualsAndHashCode.Include
  @ToString.Include
  private final String fullyQualifiedName = table.getFullyQualifiedName() + "." + name;

  @Builder.Default @ToString.Include private DataType dataType = new DataType();

  @Builder.Default @ToString.Include
  private List<ConstraintAttribute> constraints = new ArrayList<>();

  @Getter(lazy = true)
  @JsonIgnore
  private final boolean primaryKey =
      Objects.nonNull(constraints) && constraints.contains(ConstraintAttribute.PRIMARY_KEY);
}
