package io.sitoolkit.rdg.core.domain.generator;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;

public class RowDataStore {

  private List<RowData> datas = new ArrayList<>();

  private Set<Integer> dataHashCodes = new TreeSet<>();

  public void add(RowData rowData) {
    datas.add(rowData);
    dataHashCodes.add(rowData.hashCode());
  }

  public RowData get() {
    return getRandomly();
  }

  public RowData getRandomly() {
    return datas.get(nextInt());
  }

  public RowData getUniquely() {
    int index = nextInt();
    RowData rowData = datas.get(index);
    datas.remove(index);
    dataHashCodes.remove(rowData.hashCode());
    return rowData;
  }

  public boolean contains(RowData rowData) {
    return dataHashCodes.contains(rowData.hashCode());
  }

  int nextInt() {
    return ThreadLocalRandom.current().nextInt(datas.size());
  }
}
