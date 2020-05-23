package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.domain.generator.RowData;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.Setter;

public class SeqBranchNumValueGenerator implements ValueGenerator {
  @Getter @Setter private List<String> parentColumns = new ArrayList<>();

  private List<String> columnFqns;

  private int branchNumber = 1;

  private String previousParentValue = "";

  @Override
  public String generate(RowData rowData) {
    String parentValue = columnFqns.stream().map(rowData::get).collect(Collectors.joining("}-{"));

    if (parentValue.equals(previousParentValue)) {
      branchNumber++;
    } else {
      previousParentValue = parentValue;
      branchNumber = 1;
    }

    return Integer.toString(branchNumber);
  }

  @Override
  public void initialize(ColumnConfig config) {
    columnFqns =
        parentColumns.stream()
            .map(column -> config.getTableConfig().getFullyQualifiedName() + "." + column)
            .collect(Collectors.toList());
  }
}
