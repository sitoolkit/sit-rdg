package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@ToString(
    exclude = {"table", "columns"},
    doNotUseGetters = true)
public class UniqueConstraintDef {

  @JsonBackReference private TableDef table;

  @JsonProperty("columns")
  private List<String> columnNames = new ArrayList<>();

  @JsonIgnore
  @Getter(lazy = true)
  private final List<ColumnDef> columns =
      columnNames.stream()
          .map(table::findColumn)
          .filter(opt -> !opt.isEmpty())
          .map(Optional::get)
          .collect(Collectors.toList());
}
