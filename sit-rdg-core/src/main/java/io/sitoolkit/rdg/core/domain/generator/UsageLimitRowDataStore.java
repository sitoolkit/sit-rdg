package io.sitoolkit.rdg.core.domain.generator;

import lombok.Getter;
import lombok.Setter;

public class UsageLimitRowDataStore extends RowDataStoreImpl {

  @Setter private int usageCountLimit;

  private int currentIndex = 0;

  @Getter @Setter private boolean rotated = false;

  @Override
  public RowData get() {
    RowData rowData = get(currentIndex);

    rowData.setUsedCount(rowData.getUsedCount() + 1);

    if (rowData.getUsedCount() >= usageCountLimit) {
      currentIndex = currentIndex >= datas.size() - 1 ? 0 : currentIndex + 1;
      datas.stream().forEach(r -> r.setUsedCount(0));
      rotated = true;
    }

    return rowData;
  }
}
