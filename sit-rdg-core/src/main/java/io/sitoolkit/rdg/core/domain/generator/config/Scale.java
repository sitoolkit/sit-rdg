package io.sitoolkit.rdg.core.domain.generator.config;

import lombok.AccessLevel;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

public class Scale {

  @Getter(AccessLevel.PACKAGE)
  private double value = 1;

  private Scale() {}

  public long apply(long num) {
    return Math.max(1, Math.round(num * value));
  }

  @Deprecated
  public int apply(int num) {
    return (int) Math.max(1, Math.round(num * value));
  }

  public static Scale parse(String scaleStr) {
    Scale scale = new Scale();

    if (StringUtils.isEmpty(scaleStr)) {
      return scale;
    }

    double numerator = NumberUtils.toDouble(StringUtils.substringBefore(scaleStr, "/"), 1);
    double denominator = NumberUtils.toDouble(StringUtils.substringAfter(scaleStr, "/"), 1);

    scale.value = numerator / denominator;

    return scale;
  }
}
