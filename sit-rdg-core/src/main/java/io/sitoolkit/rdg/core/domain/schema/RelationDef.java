package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class RelationDef {

  @EqualsAndHashCode.Include @Getter private Set<ColumnPair> columnPairs = new HashSet<>();

  @JsonIgnore
  public List<ColumnDef> getDistinctColumns() {
    return columnPairs.stream()
        .map(ColumnPair::getColumns)
        .flatMap(Collection::stream)
        .distinct()
        .collect(Collectors.toList());
  }

  @JsonIgnore
  public List<TableDef> getTables() {
    return columnPairs.stream()
        .map(ColumnPair::getColumns)
        .flatMap(Collection::stream)
        .map(ColumnDef::getTable)
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
  public List<ColumnDef> getLeftColumns() {
    return columnPairs.stream().map(ColumnPair::getLeft).collect(Collectors.toList());
  }

  @JsonIgnore
  public List<ColumnDef> getRightColumns() {
    return columnPairs.stream().map(ColumnPair::getRight).collect(Collectors.toList());
  }

  @JsonIgnore
  public TableDef getLeftTable() {
    return columnPairs.iterator().next().getLeft().getTable();
  }

  @JsonIgnore
  public TableDef getRightTable() {
    return columnPairs.iterator().next().getRight().getTable();
  }

  @JsonIgnore
  public int getSize() {
    return getLeftColumns().size();
  }

  @JsonIgnore
  public boolean isSelfRelation() {
    return getRightTable().equals(getLeftTable());
  }

  public List<UniqueConstraintDef> getRightUniqueConstraints() {
    List<UniqueConstraintDef> rightUniques = new ArrayList<>();
    for (UniqueConstraintDef unique : getRightTable().getUniqueConstraints()) {
      if (unique.isRelated(getRightColumns())) {
        rightUniques.add(unique);
      }
    }
    return rightUniques;
  }
}
