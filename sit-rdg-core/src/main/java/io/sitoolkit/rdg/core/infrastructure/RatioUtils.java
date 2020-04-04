package io.sitoolkit.rdg.core.infrastructure;

import java.util.List;

public class RatioUtils {

  private RatioUtils() {}

  public static <T extends NormalizableRatio> T get(List<T> items) {
    int index = -1;

    while (index < 0) {
      for (int i = 0; i < items.size(); i++) {
        if (Math.random() <= items.get(i).getRatio()) {
          index = i;
        }
      }
    }

    return items.get(index);
  }

  public static void normalize(List<? extends NormalizableRatio> items) {
    double sumRatio = items.stream().mapToDouble(NormalizableRatio::getRatio).sum();

    if (sumRatio != 1) {
      items.stream().forEach(choice -> choice.setRatio(choice.getRatio() / sumRatio));
    }
  }
}
