package io.sitoolkit.rdg.core.domain.schema;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import net.sf.jsqlparser.statement.create.table.Index;

public enum ConstraintAttribute {
  PRIMARY_KEY,
  UNIQUE,
  INDEX;

  @Getter private Predicate<String> pattern;

  private ConstraintAttribute() {
    this.pattern = (s) -> StringUtils.containsIgnoreCase(s, name().replace("_", " "));
  }

  public static List<ConstraintAttribute> matchedConstraints(
      List<Index> indexes, ColumnDef column) {

    if (Objects.isNull(indexes)) {
      return Collections.emptyList();
    }

    List<Index> filtIndexes =
        indexes
            .parallelStream()
            .filter(in -> in.getColumnsNames().contains(column.getName()))
            .collect(Collectors.toList());

    return Stream.of(ConstraintAttribute.values())
        .filter(
            attr ->
                filtIndexes.parallelStream().anyMatch(in -> attr.getPattern().test(in.getType())))
        .collect(Collectors.toList());
  }
}
