package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
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
    adjustDigits();
  }

  private void adjustDigits() {

    // 一番小さい桁数に合わせる
    List<String> minDigits =
        columnPairs.stream()
            .flatMap(c -> c.getColumns().stream())
            .sorted(
                Comparator.comparing(ColumnDef::getIntegerDigit)
                    .thenComparing(ColumnDef::getDecimalDigit))
            .map(ColumnDef::getArgs)
            .filter(Objects::nonNull)
            .findFirst()
            .orElse(Collections.emptyList());

    columnPairs.stream().flatMap(c -> c.getColumns().stream()).forEach(c -> c.setArgs(minDigits));
  }

  public boolean containsAnyInPair(ColumnPair pair) {
    return getDistinctColumns().parallelStream().anyMatch(col -> pair.getColumns().contains(col));
  }
}
