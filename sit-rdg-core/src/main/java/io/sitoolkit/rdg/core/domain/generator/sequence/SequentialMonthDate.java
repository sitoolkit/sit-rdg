package io.sitoolkit.rdg.core.domain.generator.sequence;

import java.time.LocalDate;

public class SequentialMonthDate extends SequentialDate {

  public SequentialMonthDate(LocalDate startDate, LocalDate endDate, String pattern) {
    super(startDate, endDate, pattern);
  }

  @Override
  public String nextVal() {
    if (isReachTop()) {
      if (hasParent()) {
        getParentSequence().nextVal();
      }
      initVal();
    } else {
      currentDate = currentDate.plusMonths(1L);
    }
    return currentVal();
  }
}