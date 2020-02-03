package io.sitoolkit.rdg.core.domain.generator;

import java.util.Comparator;
import java.util.Map;
import java.util.TreeMap;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;

public class RandomValueGroup {

  Map<ColumnDef, String> valueMap =
      new TreeMap<>(Comparator.comparing(ColumnDef::getFullyQualifiedName));

  void setColumnValue(ColumnDef column, String value) {
    valueMap.put(column, value);
  }

  void generateEmptyColumnValue(RelationDef relation) {
    relation.getDistinctColumns().stream()
        .forEach(column -> valueMap.putIfAbsent(column, RandomValueUtils.generate(column)));
  }
}
