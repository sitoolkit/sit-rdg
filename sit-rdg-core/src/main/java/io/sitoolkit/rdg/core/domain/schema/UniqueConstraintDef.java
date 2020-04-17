package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
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
      table.getColumns().stream()
          .filter(column -> columnNames.contains(column.getName()))
          .collect(Collectors.toList());

  //   @JsonIgnore
  //   @Getter(lazy = true)
  //   private final List<String> fullyQualifiedColumnNames =
  //       table.getColumns().stream()
  //           .map(ColumnDef::getFullyQualifiedName)
  //           .collect(Collectors.toList());

  public boolean isRelated(List<ColumnDef> checkColumns) {
    for (ColumnDef column : checkColumns) {
      if (getColumns().contains(column)) {
        return true;
      }
    }
    return false;
  }
}
