package io.sitoolkit.rdg.core.domain.generator.sequence;

import java.util.Arrays;
import java.util.stream.IntStream;

public class SequentialString extends AbstractSequence {

  private char[] valueChars;

  private char minCode;
  private char maxCode;

  private int length;

  public SequentialString(int length, char minCode, char maxCode) {
    this.minCode = minCode;
    this.maxCode = maxCode;

    this.length = length;
    this.valueChars = new char[length];
    Arrays.fill(this.valueChars, minCode);
  }

  @Override
  public boolean isReachTop() {
    return IntStream.range(0, valueChars.length)
        .mapToObj(i -> valueChars[i])
        .allMatch(c -> c == maxCode);
  }

  @Override
  public String currentVal() {
    return String.valueOf(valueChars);
  }

  @Override
  public String nextVal() {
    if (isReachTop()) {
      if (hasParent()) {
        getParentSequence().nextVal();
      }
      initVal();
    } else {
      increment(length - 1);
    }

    return currentVal();
  }

  @Override
  public void initVal() {
    Arrays.fill(this.valueChars, minCode);
  }

  private void increment(int digit) {
    if (digit < 0) {
      return;
    }
    if ('a' <= maxCode) {
      if ('Z' == valueChars[digit]) {
        valueChars[digit] = 'a';
      } else if ('9' == valueChars[digit]) {
        valueChars[digit] = 'A';
      } else {
        valueChars[digit]++;
      }
    } else if ('A' <= maxCode) {
      if ('9' == valueChars[digit]) {
        valueChars[digit] = 'A';
      } else {
        valueChars[digit]++;
      }
    } else {
      valueChars[digit]++;
    }

    if (maxCode < valueChars[digit]) {
      valueChars[digit] = minCode;
      increment(digit - 1);
    }

    return;
  }
}
