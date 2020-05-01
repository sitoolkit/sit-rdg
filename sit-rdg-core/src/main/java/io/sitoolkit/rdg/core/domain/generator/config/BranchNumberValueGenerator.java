package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

public class BranchNumberValueGenerator implements ValueGenerator {

  @Getter @Setter private List<String> parentColumns = new ArrayList<>();

  private Map<Integer, Integer> branchMap = new HashMap<>();

  private List<String> columnFqns;

  @Override
  public String generate(RowData rowData) {
    int key = columnFqns.stream().map(rowData::get).collect(Collectors.toList()).hashCode();

    int branchNumber = branchMap.computeIfAbsent(key, k -> 0);

    branchNumber++;

    branchMap.put(key, branchNumber);

    return Integer.toString(branchNumber);
  }

  @Override
  public void initialize(ColumnConfig config) {
    columnFqns =
        parentColumns.stream()
            .map(column -> config.getTableConfig().getFullQualifiedName() + "." + column)
            .collect(Collectors.toList());
  }
}
