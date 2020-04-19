package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.MultiplicityConfig;
import io.sitoolkit.rdg.core.infrastructure.NormalizableRatio;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MultiRowDataStore implements RowDataStore {

  private List<DataStoreGroup> groups = new ArrayList<>();

  private int currentIndex = 0;

  public RowData get() {
    DataStoreGroup group = groups.get(currentIndex);
    RowData rowData = group.getDataStore().get();

    if (group.getDataStore().isRotated()) {
      currentIndex = currentIndex >= groups.size() - 1 ? 0 : currentIndex + 1;
      group.getDataStore().setRotated(false);
    }

    return rowData;
  }

  @Override
  public void add(RowData rowData) {
    DataStoreGroup group = RatioUtils.get(groups);
    group.getDataStore().add(rowData);
  }

  @Override
  public boolean contains(RowData rowData) {
    for (DataStoreGroup group : groups) {
      if (group.getDataStore().contains(rowData)) {
        return true;
      }
    }
    return false;
  }

  public void initialize(List<MultiplicityConfig> configs) {

    for (MultiplicityConfig config : configs) {
      UsageLimitRowDataStore dataStore = new UsageLimitRowDataStore();
      dataStore.setUsageCountLimit(config.getMultiplicity());

      DataStoreGroup group = new DataStoreGroup();
      group.setRatio(config.getRatio());
      group.setDataStore(dataStore);

      groups.add(group);
    }
  }

  @Data
  class DataStoreGroup implements NormalizableRatio {
    double ratio;
    UsageLimitRowDataStore dataStore;
  }

  @Override
  public void clear() {
    groups.clear();
  }

  @Override
  public void setUp() {
    Iterator<DataStoreGroup> groupItr = groups.iterator();
    while (groupItr.hasNext()) {
      DataStoreGroup group = groupItr.next();

      if (group.getDataStore().isEmpty()) {
        groupItr.remove();
        log.debug("Removed empty group:{} ", group);
      }
    }
  }
}
