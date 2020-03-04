package io.sitoolkit.rdg.core.domain.generator.sequence;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SequentialDate extends AbstractSequence {

  private LocalDate startDate;

  private LocalDate endDate;

  private LocalDate currentDate;

  private DateTimeFormatter formatter;

  public SequentialDate(LocalDate startDate, LocalDate endDate, String pattern) {

    this.startDate = startDate;
    this.endDate = endDate;

    formatter = DateTimeFormatter.ofPattern(pattern);
    currentDate = startDate;
  }

  @Override
  public boolean isReachTop() {
    return currentDate.isEqual(endDate) || currentDate.isAfter(endDate);
  }

  @Override
  public String currentVal() {
    return currentDate.format(formatter);
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

  @Override
  public void setVal(String value) {
    currentDate = LocalDate.parse(value, formatter);
  }
}
