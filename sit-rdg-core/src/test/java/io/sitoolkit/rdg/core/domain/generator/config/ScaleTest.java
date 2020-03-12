package io.sitoolkit.rdg.core.domain.generator.config;

import org.junit.Assert;
import org.junit.Test;

public class ScaleTest {

  @Test
  public void test() {
    Scale scale = new Scale("/10");
    Assert.assertEquals("/", scale.getMark());
    Assert.assertEquals(10, scale.getValue());
  }

  @Test
  public void testScaleDown() {
    Scale scale = new Scale("/5");
    Assert.assertEquals(20, scale.apply(100));
    Assert.assertEquals(1, scale.apply(4));
    Assert.assertEquals(1, scale.apply(9));
    Assert.assertEquals(1, scale.apply(2));
    Assert.assertEquals(1, scale.apply(1));
  }
}