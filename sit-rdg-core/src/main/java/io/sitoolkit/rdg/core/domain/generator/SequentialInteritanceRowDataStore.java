package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.Alignment;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@RequiredArgsConstructor
public class SequentialInteritanceRowDataStore implements RowDataStore {

  @Getter(value = AccessLevel.PROTECTED)
  private final ColumnDef column;

  @Getter(value = AccessLevel.PROTECTED)
  private long max = Long.MIN_VALUE;

  @Getter(value = AccessLevel.PROTECTED)
  private long min = Long.MAX_VALUE;

  @Getter(value = AccessLevel.PROTECTED)
  private Alignment alignment;

  public SequentialInteritanceRowDataStore(ColumnDef columnDef, Alignment alignment) {
    this.column = columnDef;
    this.alignment = alignment;
  }

  @Override
  public RowData get() {
    long pseudoStoredValue = ThreadLocalRandom.current().nextLong(min, max + 1);
    RowData rowData = new RowData();

    String val = Long.toString(pseudoStoredValue);

    if (Objects.nonNull(alignment)) {
      rowData.put(column, StringUtils.leftPad(val, alignment.getLength(), alignment.getPadChar()));
    } else {
      rowData.put(column, val);
    }

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
