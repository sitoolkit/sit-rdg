package io.sitoolkit.rdg.core.domain.generator.config;

import org.apache.commons.lang3.StringUtils;

public interface AlignmentCharValueGenerator {

  Alignment getAlign();

  default String padding(String val) {
    return StringUtils.leftPad(val, getAlign().getLength(), getAlign().getPadChar());
  }
}
