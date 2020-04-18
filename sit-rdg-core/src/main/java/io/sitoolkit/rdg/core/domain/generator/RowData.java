package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class RowData {

  @EqualsAndHashCode.Include private Map<String, String> valueMap = new HashMap<>();

  public void put(ColumnDef column, String value) {
    put(column.getFullyQualifiedName(), value);
  }

  private void put(String column, String value) {
    String replacedValue = valueMap.put(column, value);

    if (replacedValue != null && !replacedValue.equals(value)) {
      throw new IllegalStateException("Value of " + column + " already exists");
    }
  }

  public void putAll(RowData rowData) {
    for (Entry<String, String> entry : rowData.valueMap.entrySet()) {
      put(entry.getKey(), entry.getValue());
    }
  }

  public boolean contains(ColumnDef column) {
    return valueMap.containsKey(column.getFullyQualifiedName());
  }

  public void putAllMainToSub(RowData rowData, RelationDef relation) {
    for (ColumnPair pair : relation.getColumnPairs()) {
      put(pair.getRight(), rowData.get(pair.getLeft()));
    }
  }

  public String get(ColumnDef column) {
    return valueMap.get(column.getFullyQualifiedName());
  }

  public void mergeRelatedData(RowData relatedRowData, RelationDef relation) {
    for (ColumnPair pair : relation.getColumnPairs()) {
      put(pair.getRight(), relatedRowData.get(pair.getLeft()));
    }
  }

  public List<Object> toList(List<ColumnDef> order) {
    return order.stream().map(this::get).collect(Collectors.toList());
  }

  @Deprecated
  public boolean containsAsSub(RelationDef relation) {
    for (ColumnDef column : relation.getRightColumns()) {
      if (!contains(column)) {
        return false;
      }
    }

    return true;
  }

  public boolean containsAll(List<ColumnDef> columns) {
    for (ColumnDef column : columns) {
      if (!contains(column)) {
        return false;
      }
    }

    return true;
  }

  public RowData filter(List<ColumnDef> columns) {
    RowData newRowData = new RowData();
    for (ColumnDef column : columns) {
      String value = get(column);
      if (value != null) {
        newRowData.put(column, value);
      }
    }
    return newRowData;
  }

  public int size() {
    return valueMap.size();
  }
}
