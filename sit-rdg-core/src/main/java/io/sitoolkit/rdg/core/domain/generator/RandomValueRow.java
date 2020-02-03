package io.sitoolkit.rdg.core.domain.generator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;

public class RandomValueRow {

  Map<ColumnDef, Object> generatedValueMap = new LinkedHashMap<>();

  public void put(ColumnDef column, Object value) {
    generatedValueMap.put(column, value);
  }

  public List<ColumnDef> getColumns() {
    return new ArrayList<>(generatedValueMap.keySet());
  }

  public List<Object> getLineValues() {
    return new ArrayList<>(generatedValueMap.values());
  }

  public List<String> getPrimaryKeyValues() {
    return generatedValueMap.entrySet().stream()
        .filter(e -> Objects.nonNull(e.getKey().getConstraints()))
        .filter(e -> e.getKey().isPrimaryKey())
        .sorted(Comparator.comparing(Map.Entry::getKey))
        .filter(e -> Objects.nonNull(e.getValue()))
        .map(e -> e.getValue().toString())
        .collect(Collectors.toList());
  }

  @Override
  public int hashCode() {
    List<String> primaryKeyValues = getPrimaryKeyValues();
    if (primaryKeyValues.isEmpty()) {
      return getLineValues().hashCode();
    }
    return primaryKeyValues.hashCode();
  }

  @Override
  public boolean equals(Object other) {

    if (other instanceof RandomValueRow) {
      RandomValueRow otherRow = (RandomValueRow) other;
      if (otherRow.getColumns().containsAll(this.getColumns())) {
        return hashCode() == other.hashCode();
      }
    }
    return false;
  }
}
