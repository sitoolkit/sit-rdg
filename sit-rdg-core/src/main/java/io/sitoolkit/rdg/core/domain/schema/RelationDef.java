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
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@EqualsAndHashCode(onlyExplicitlyIncluded = true, doNotUseGetters = true)
@NoArgsConstructor
public class RelationDef {

  @Getter @JsonBackReference private SchemaDef schema;

  @JsonManagedReference @EqualsAndHashCode.Include @Getter @ToString.Include
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

  @Getter(lazy = true)
  @JsonIgnore
  private final List<ColumnDef> mainColumns =
      columnPairs.stream().map(ColumnPair::getMain).collect(Collectors.toList());

  @Getter(lazy = true)
  @JsonIgnore
  private final List<ColumnDef> subColumns =
      columnPairs.stream().map(ColumnPair::getSub).collect(Collectors.toList());

  @Getter(lazy = true)
  @JsonIgnore
  private final TableDef mainTable = columnPairs.iterator().next().getMain().getTable();

  @Getter(lazy = true)
  @JsonIgnore
  private final TableDef subTable = columnPairs.iterator().next().getSub().getTable();

  @Getter(lazy = true)
  @JsonIgnore
  private final int size = getMainColumns().size();

  @Getter(lazy = true)
  @JsonIgnore
  private final boolean selfRelation = getSubTable().equals(getMainTable());

  public void addAllPairs(Collection<ColumnPair> pairs) {
    columnPairs.addAll(pairs);
  }

  private List<UniqueConstraintDef> initMainUniqueConstraints() {
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
