package io.sitoolkit.rdg.core.domain.schema;

import java.util.List;
import java.util.SortedSet;
import java.util.stream.Collectors;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SchemaDef {

  @JsonInclude(Include.NON_EMPTY)
  @JsonProperty("schemaName")
  private String name;

  @JsonProperty("tables")
  @JsonManagedReference
  private SortedSet<TableDef> tables;

  @JsonIgnore
  public List<ColumnDef> getColumns() {
    return getTables().stream()
        .map(TableDef::getColumns)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }
}
