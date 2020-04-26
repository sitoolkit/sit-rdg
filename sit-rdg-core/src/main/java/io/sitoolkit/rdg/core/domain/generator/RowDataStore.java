package io.sitoolkit.rdg.core.domain.generator;

public interface RowDataStore {

  RowData get();

  void add(RowData rowData);

  boolean contains(RowData rowData);

  default void setUp() {
    // NOP
  }

  void clear();
}
