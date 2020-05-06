package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class SequentialInteritanceRowDataStore implements RowDataStore {

  @Getter(value = AccessLevel.PROTECTED)
  private final ColumnDef column;

  @Getter(value = AccessLevel.PROTECTED)
  private long max = Long.MIN_VALUE;

  @Getter(value = AccessLevel.PROTECTED)
  private long min = Long.MAX_VALUE;

  @Override
  public RowData get() {
    long pseudoStoredValue = ThreadLocalRandom.current().nextLong(min, max + 1);
    RowData rowData = new RowData();
    rowData.put(column, Long.toString(pseudoStoredValue));
    return rowData;
  }

  @Override
  public void add(RowData rowData) {
    long newValue = row2val(rowData);

    max = Math.max(newValue, max);
    min = Math.min(newValue, min);
  }

  @Override
  public boolean contains(RowData rowData) {
    long checkValue = row2val(rowData);
    return min <= checkValue && checkValue <= max;
  }

  @Override
  public void clear() {
    // NOP
  }

  private long row2val(RowData rowData) {
    return Long.parseLong(rowData.get(column));
  }
}
