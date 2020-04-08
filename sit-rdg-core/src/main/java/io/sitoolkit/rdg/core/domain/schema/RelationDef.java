package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
@EqualsAndHashCode
@NoArgsConstructor
public class RelationDef {

  @Getter private Set<ColumnPair> columnPairs = new HashSet<>();

  @JsonIgnore
  public List<ColumnDef> getDistinctColumns() {
    return columnPairs.stream()
        .flatMap(c -> c.getColumns().stream())
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
}
