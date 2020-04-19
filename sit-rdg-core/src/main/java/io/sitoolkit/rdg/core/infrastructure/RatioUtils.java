package io.sitoolkit.rdg.core.infrastructure;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class RatioUtils {

  private RatioUtils() {}

  public static <T extends NormalizableRatio> T get(Collection<T> items) {
    ThreadLocalRandom random = ThreadLocalRandom.current();

    if (items.isEmpty()) {
      throw new IllegalArgumentException("items must not be empty");
    }

    while (true) {
      for (T item : items) {
        double ratio = random.nextDouble();
        if (ratio <= item.getRatio()) {
          return item;
        }
      }
    }
  }

  public static void normalize(List<? extends NormalizableRatio> items) {
    double sumRatio = items.stream().mapToDouble(NormalizableRatio::getRatio).sum();

    if (sumRatio != 1) {
      items.stream().forEach(choice -> choice.setRatio(choice.getRatio() / sumRatio));
    }

    Collections.sort(items, Comparator.comparing(NormalizableRatio::getRatio));
  }
}
