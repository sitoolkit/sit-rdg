package io.sitoolkit.rdg.core.domain.generator.config;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;

public class Scale {

  @Getter
  private String scaleStr;

  @Getter(lazy = true)
  private final String mark = StringUtils.substring(scaleStr, 0, 1);

  @Getter(lazy = true)
  private final int value = Integer.parseInt(StringUtils.substring(scaleStr, 1));

  public Scale(String scaleStr) {
    this.scaleStr = scaleStr;
  }

  public static Scale createEqualScale() {
    return new Scale("/1");
  }

  public int apply(int num) {

    switch (getMark()) {
      // Divide
      case "/":
      {
        if (num == 1) {
          return num;
        }
        return num / getValue();
      }
      default: return num;
    }
  }
}
