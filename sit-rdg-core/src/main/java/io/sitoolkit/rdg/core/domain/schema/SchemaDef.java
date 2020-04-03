package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
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

  @JsonManagedReference private SortedSet<TableDef> tables;

  @Builder.Default private List<RelationDef> relations = new ArrayList<>();

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
            column ->
                StringUtils.equalsIgnoreCase(column.getFullyQualifiedName(), qualifiedColumnName))
        .findFirst();
  }

  @JsonIgnore
  public Optional<TableDef> findTable(String tableName) {
    return tables.stream()
        .filter(table -> StringUtils.equalsIgnoreCase(table.getName(), tableName))
        .findFirst();
  }

  public boolean equalsName(String name) {
    if (StringUtils.isEmpty(this.name)) {
      return StringUtils.isEmpty(name);
    }

    return StringUtils.equalsIgnoreCase(this.name, name);
  }

  public void afterDeserialize() {
    relations.stream().forEach(this::resolveRelationReferenceOfColumn);
  }

  void resolveRelationReferenceOfColumn(RelationDef relation) {
    Set<ColumnDef> columnsInSchema =
        relation.getDistinctColumns().stream()
            .map(ColumnDef::getFullyQualifiedName)
            .map(this::findColumnByQualifiedName)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());

    for (ColumnDef columnInSchema : columnsInSchema) {
      for (ColumnPair pair : relation.getColumnPairs()) {
        pair.getColumns().remove(columnInSchema);
        pair.getColumns().add(columnInSchema);
        columnInSchema.getRelations().add(relation);
      }
    }
  }
}
