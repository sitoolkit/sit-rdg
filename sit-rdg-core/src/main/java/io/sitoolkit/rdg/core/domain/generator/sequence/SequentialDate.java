package io.sitoolkit.rdg.core.domain.generator.sequence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SequentialDate extends AbstractSequence {

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalDate currentDate;

  private String pattern;

  public SequentialDate(LocalDate startDate, LocalDate endDate, String pattern) {

    this.startDate = startDate;
    this.endDate = endDate;
    this.pattern = pattern;

    currentDate = startDate;
  }

  @Override
  public boolean isReachTop() {
    return currentDate.isEqual(endDate) || currentDate.isAfter(endDate);
  }

  @Override
  public String currentVal() {
    return currentDate.format(DateTimeFormatter.ofPattern(pattern));
  }

  @Override
  public String nextVal() {
    if (isReachTop()) {
      if (hasParent()) {
        getParentSequence().nextVal();
      }
      initVal();
    } else {
      currentDate = currentDate.plusDays(1L);
    }
    return currentVal();
  }

  @Override
  public void initVal() {
    currentDate = startDate;
  }
}
