package io.sitoolkit.rdg.core.domain.generator;


public enum NopRowDataStore implements RowDataStore {
  INSTANCE;

  @Override
  public RowData get() {
    throw new UnsupportedOperationException();
  }

  @Override
  public void add(RowData rowData) {
    // NOP
  }

  @Override
  public boolean contains(RowData rowData) {
    return false;
  }

  @Override
  public void clear() {
    // NOP
  }
}
