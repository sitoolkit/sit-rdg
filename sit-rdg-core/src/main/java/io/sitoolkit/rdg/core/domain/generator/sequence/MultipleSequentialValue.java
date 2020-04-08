package io.sitoolkit.rdg.core.domain.generator.sequence;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.lang3.StringUtils;

public class MultipleSequentialValue extends AbstractSequence {

  private Map<ColumnDef, AbstractSequence> sequenceByPkColumn = new HashMap<>();

  private AbstractSequence leaf;

  public MultipleSequentialValue(List<ColumnDef> pkColumns) {

    if (pkColumns.isEmpty()) {
      return;
    }
    AbstractSequence parentSeq = null;

    for (ColumnDef key : pkColumns) {
      AbstractSequence currentSeq = null;

      switch (key.getDataType().getName()) {
        case DECIMAL:
          currentSeq = new SequentialString(key.getDataType().getIntegerDigit(), '0', '9');
          break;
        case CHAR:
        case VARCHAR:
          currentSeq = new SequentialString(key.getDataType().getIntegerDigit(), '0', 'z');
          break;
        case DATE:
          LocalDate today = LocalDate.now();
          currentSeq =
              new SequentialDate(today.minusMonths(7L), today.plusMonths(1L), "yyyy-MM-dd");
          break;
        case TIMESTAMP:
          LocalDateTime nowtime = LocalDate.now().atTime(0, 0, 0);
          currentSeq =
              new SequentialDateTime(
                  nowtime.minusMonths(7L), nowtime.plusMonths(1L), "yyyy-MM-dd HH:mm:ss.SSS");
          break;
        default:
      }

      sequenceByPkColumn.put(key, currentSeq);
      currentSeq.setParentSequence(parentSeq);
      parentSeq = currentSeq;
    }

    leaf = sequenceByPkColumn.get(pkColumns.get(pkColumns.size() - 1));
  }

  @Override
  public boolean isReachTop() {
    if (Objects.isNull(leaf)) {
      return false;
    }
    return leaf.isReachTop();
  }

  @Override
  public String currentVal() {
    if (Objects.isNull(leaf)) {
      return StringUtils.EMPTY;
    }
    return leaf.currentVal();
  }

  @Override
  public String nextVal() {
    if (Objects.isNull(leaf)) {
      return StringUtils.EMPTY;
    }
    return leaf.nextVal();
  }

  @Override
  public void initVal() {
    sequenceByPkColumn.values().forEach(seq -> seq.initVal());
  }

  @Override
  public void setVal(String value) {
    // NOP
  }

  public boolean containsPkColumn(ColumnDef col) {
    return sequenceByPkColumn.containsKey(col);
  }

  public AbstractSequence getSequenceByPkColumn(ColumnDef column) {
    return sequenceByPkColumn.get(column);
  }

  public void putIfPresent(ColumnDef column, String value) {
    if (sequenceByPkColumn.containsKey(column)) {
      sequenceByPkColumn.get(column).setVal(value);
    }
  }
}
