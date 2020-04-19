package io.sitoolkit.rdg.core.domain.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import lombok.ToString;

@ToString(onlyExplicitlyIncluded = true, doNotUseGetters = true)
public class RowDataStoreImpl implements RowDataStore {

  @ToString.Include protected List<RowData> datas = new ArrayList<>();

  private Set<Integer> dataHashCodes = new TreeSet<>();

  public void add(RowData rowData) {
    datas.add(rowData);
    dataHashCodes.add(rowData.hashCode());
  }

  public RowData get() {
    return datas.get(getIndex());
  }

  public boolean contains(RowData rowData) {
    return dataHashCodes.contains(rowData.hashCode());
  }

  public int getIndex() {
    return ThreadLocalRandom.current().nextInt(datas.size());
  }

  public RowData remove(int index) {
    RowData rowData = datas.remove(index);
    dataHashCodes.remove(rowData.hashCode());
    return rowData;
  }

  public RowData get(int index) {
    return datas.get(index);
  }

  protected boolean isEmpty() {
    return datas.isEmpty();
  }
}
