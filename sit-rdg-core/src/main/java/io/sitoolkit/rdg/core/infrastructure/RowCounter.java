package io.sitoolkit.rdg.core.infrastructure;

import java.util.ArrayList;
import java.util.List;

public class RowCounter {

  private long goal;

  private List<Long> checkPoints = new ArrayList<>();

  private long checkPoint = -1;

  public boolean isCheckPoint(long count) {
    return count == checkPoint;
  }

  public long getProgressRate(long count) {
    return Math.round(((double) count / goal) * 100);
  }

  public void next() {
    if (!checkPoints.isEmpty()) {
      checkPoint = checkPoints.remove(0);
    }
  }

  public void init(long goal) {
    init(goal, 10, 10000);
  }

  public void init(long goal, int checkPointCount, long minGoal) {
    this.goal = goal;
    checkPoints.clear();
    checkPoint = -1;

    if (goal < minGoal) {
      return;
    }

    for (int i = 1; i < checkPointCount; i++) {
      checkPoints.add(goal / checkPointCount * i);
    }
    next();
  }
}
