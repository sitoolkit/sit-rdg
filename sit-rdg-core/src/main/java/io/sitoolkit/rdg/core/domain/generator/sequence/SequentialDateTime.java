package io.sitoolkit.rdg.core.domain.generator.sequence;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SequentialDateTime extends AbstractSequence {

  private LocalDateTime startDateTime;

  private LocalDateTime endDateTime;

  private LocalDateTime currentDateTime;

  private String pattern;

  public SequentialDateTime(
      LocalDateTime startDateTime, LocalDateTime endDateTime, String pattern) {

    this.startDateTime = startDateTime;
    this.endDateTime = endDateTime;
    this.pattern = pattern;

    currentDateTime = startDateTime;
  }

  @Override
  public boolean isReachTop() {
    return currentDateTime.isEqual(endDateTime) || currentDateTime.isAfter(endDateTime);
  }

  @Override
  public String currentVal() {
    return currentDateTime.format(DateTimeFormatter.ofPattern(pattern));
  }

  @Override
  public String nextVal() {
    if (isReachTop()) {
      if (hasParent()) {
        getParentSequence().nextVal();
      }
      initVal();
    } else {
      currentDateTime = currentDateTime.plusHours(12L);
    }
    return currentVal();
  }

  @Override
  public void initVal() {
    currentDateTime = startDateTime;
  }
}
