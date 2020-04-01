package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import java.util.Optional;
import java.util.SortedSet;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

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

  private List<RelationDef> relations;

  @JsonIgnore
  public List<ColumnDef> getColumns() {
    return getTables().stream()
        .map(TableDef::getColumns)
        .flatMap(List::stream)
        .collect(Collectors.toList());
  }

  public Optional<ColumnDef> findColumnByQualifiedName(String qualifiedColumnName) {
    return getColumns().stream()
        .filter(
            column -> {
              System.out.println(column);
              System.out.println(qualifiedColumnName);
              return StringUtils.equalsIgnoreCase(
                  column.getFullyQualifiedName(), qualifiedColumnName);
            })
        .findFirst();
  }

  @JsonIgnore
  public Optional<TableDef> findTable(String tableName) {
    return tables.stream()
        .filter(table -> StringUtils.equalsIgnoreCase(table.getName(), tableName))
        .findFirst();
  }
}
