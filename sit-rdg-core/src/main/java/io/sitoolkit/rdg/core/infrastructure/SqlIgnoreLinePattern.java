package io.sitoolkit.rdg.core.infrastructure;

import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import lombok.Getter;

public enum SqlIgnoreLinePattern {
  COMMENT_ON("^(comment on)"),
  COMMENTS("--+");

  @Getter private Predicate<String> pattern;

  private SqlIgnoreLinePattern(String regex) {
    Pattern pattern = Pattern.compile(regex);
    this.pattern = pattern.asPredicate().negate();
  }

  public static Predicate<String> chainAllPattern() {
    return Stream.of(SqlIgnoreLinePattern.values())
        .map(SqlIgnoreLinePattern::getPattern)
        .reduce(Predicate::and)
        .orElse(t -> false);
  }
}
