package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
public class RelationDef {

  @Getter @JsonBackReference private SchemaDef schema;

  @JsonManagedReference @EqualsAndHashCode.Include @Getter @Setter @ToString.Include
  private List<ColumnPair> columnPairs = new ArrayList<>();

  @JsonIgnore
  @Getter(lazy = true)
  private final List<UniqueConstraintDef> mainUniqueConstraints = initMainUniqueConstraints();

  @Getter(lazy = true)
  @JsonIgnore
  private final List<UniqueConstraintDef> subUniqueConstraints = initSubUniqueConstraints();

  @Getter(lazy = true)
  @JsonIgnore
  private final List<TableDef> tables =
      columnPairs.stream()
          .map(ColumnPair::getColumns)
          .flatMap(Collection::stream)
          .map(ColumnDef::getTable)
          .distinct()
          .collect(Collectors.toList());

  @JsonIgnore
  public List<ColumnDef> getDistinctColumns() {
    return columnPairs.stream()
        .map(ColumnPair::getColumns)
        .flatMap(Collection::stream)
        .distinct()
        .collect(Collectors.toList());
  }

  public RelationDef(ColumnPair pair) {
    columnPairs.add(pair);
  }

  public void addAllPairs(Collection<ColumnPair> pairs) {
    columnPairs.addAll(pairs);
  }

  public boolean containsAnyInPair(ColumnPair pair) {
    return getDistinctColumns().parallelStream().anyMatch(col -> pair.getColumns().contains(col));
  }

  @JsonIgnore
  public List<ColumnDef> getMainColumns() {
    return columnPairs.stream().map(ColumnPair::getMain).collect(Collectors.toList());
  }

  @JsonIgnore
  public List<ColumnDef> getSubColumns() {
    return columnPairs.stream().map(ColumnPair::getSub).collect(Collectors.toList());
  }

  @JsonIgnore
  public TableDef getMainTable() {
    return columnPairs.iterator().next().getMain().getTable();
  }

  @JsonIgnore
  public TableDef getSubTable() {
    return columnPairs.iterator().next().getSub().getTable();
  }

  @JsonIgnore
  public int getSize() {
    return getMainColumns().size();
  }

  @JsonIgnore
  public boolean isSelfRelation() {
    return getSubTable().equals(getMainTable());
  }

  @JsonIgnore
  public List<UniqueConstraintDef> initMainUniqueConstraints() {
    List<UniqueConstraintDef> mainUniques = new ArrayList<>();
    for (UniqueConstraintDef unique : getMainTable().getUniqueConstraints()) {
      if (unique.getColumns().equals(getMainColumns())) {
        mainUniques.add(unique);
      }
    }
    return mainUniques;
  }

  private List<UniqueConstraintDef> initSubUniqueConstraints() {
    List<UniqueConstraintDef> subUniques = new ArrayList<>();
    for (UniqueConstraintDef unique : getSubTable().getUniqueConstraints()) {
      if (unique.getColumns().equals(getSubColumns())) {
        subUniques.add(unique);
      }
    }
    return subUniques;
  }
}
