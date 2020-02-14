package io.sitoolkit.rdg.core.domain.generator.sequence;

import java.util.Objects;

import lombok.Data;

@Data
public abstract class AbstractSequence {

  private AbstractSequence parentSequence;

  public boolean isReachTop() {
    return !(hasParent() && !getParentSequence().isReachTop());
  }

  public boolean hasParent() {
    return Objects.nonNull(parentSequence);
  }

  public abstract String currentVal();

  public abstract String nextVal();

  public abstract void initVal();
}
