package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class UniqueDataStore {

  private Map<UniqueConstraintDef, RowDataStore> map = new HashMap<>();

  public boolean contains(UniqueConstraintDef unique, RowData rowData) {
    RowDataStore dataStore = map.get(unique);
    if (dataStore == null) {
      return false;
    }
    return dataStore.contains(rowData);
  }

  public void put(UniqueConstraintDef unique, RowData rowData) {
    map.computeIfAbsent(unique, k -> new RowDataStore()).add(rowData);
  }

  public void putAll(Collection<UniqueConstraintDef> uniques, RowData rowData) {
    for (UniqueConstraintDef unique : uniques) {
      put(unique, rowData);
    }
  }

  public boolean containsAny(Collection<UniqueConstraintDef> uniques, RowData rowData) {
    for (UniqueConstraintDef unique : uniques) {
      if (contains(unique, rowData)) {
        return true;
      }
    }
    return false;
  }
}
