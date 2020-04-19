package io.sitoolkit.rdg.core.infrastructure;

import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.hamcrest.Matchers;
import org.junit.Test;

@Slf4j
public class RatioUtilsTest {

  @Test
  public void test() {
    RatioImpl r1 = RatioImpl.builder().ratio(0.6).build();
    RatioImpl r2 = RatioImpl.builder().ratio(0.3).build();
    RatioImpl r3 = RatioImpl.builder().ratio(0.1).build();

    List<RatioImpl> items = Arrays.asList(r1, r2, r3);
    Collections.shuffle(items);

    for (int i = 0; i < 10; i++) {
      RatioImpl selected = RatioUtils.get(items);
      log.debug("Selected: {}", selected.ratio);
      selected.count++;
    }

    assertThat(r1.count, Matchers.greaterThanOrEqualTo(r2.count));
    assertThat(r2.count, Matchers.greaterThanOrEqualTo(r3.count));
  }

  @Data
  @Builder
  static class RatioImpl implements NormalizableRatio {
    double ratio;
    int count;
  }
}
