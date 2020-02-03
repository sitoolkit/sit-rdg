package io.sitoolkit.rdg.core.domain.generator;

import java.util.Comparator;
import java.util.Map;
import java.util.stream.Collectors;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.TableDef;

public class TableComparator implements Comparator<TableDef> {

  private Map<String, Integer> priorityRankMap;

  public TableComparator(GeneratorConfig setting) {
    this.priorityRankMap =
        setting.getSchemaConfigs().stream()
            .flatMap(s -> s.getTableConfigs().stream())
            .collect(Collectors.toMap(t -> t.getFullQualifiedName(), t -> t.getPriorityRank()));
  }

  @Override
  public int compare(TableDef o1, TableDef o2) {

    Integer o1Rank = priorityRankMap.getOrDefault(o1.getFullyQualifiedName(), Integer.MAX_VALUE);
    Integer o2Rank = priorityRankMap.getOrDefault(o2.getFullyQualifiedName(), Integer.MAX_VALUE);

    int result = o1Rank - o2Rank;
    if (result == 0) {
      result = o1.compareTo(o2);
    }

    return result;
  }
}
