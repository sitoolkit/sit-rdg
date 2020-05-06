package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.MultiplicityConfig;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.infrastructure.RatioUtils;
import java.util.ArrayList;
import java.util.List;

public class MultiplicityRowDataStore extends SequentialInteritanceRowDataStore {

  private final List<MultiplicityConfig> multiplicities;

  private long sequence = 0;

  private int multiplicity = 0;

  private int index = 0;

  public MultiplicityRowDataStore(ColumnDef column, List<MultiplicityConfig> multiplicities) {
    super(column);
    this.multiplicities = new ArrayList<>(multiplicities);
  }

  @Override
  public RowData get() {
    RowData rowData = new RowData();
    rowData.put(getColumn(), Long.toString(sequence));

    multiplicity++;

    if (multiplicity >= multiplicities.get(index).getMultiplicity()) {
      sequence++;
      multiplicity = 0;
      index = Math.min(index + 1, multiplicities.size() - 1);
    }

    return rowData;
  }

  @Override
  public void setUp() {
    RatioUtils.normalize(multiplicities);
    sequence = getMin();
  }
}
