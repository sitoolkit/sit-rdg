package io.sitoolkit.rdg.core.domain.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public enum DataTypeName {
  CHAR(ArgsResolverCharImpl.instance, "CHARACTER", "CHARACTER_VARYING"),
  VARCHAR(ArgsResolverCharImpl.instance, "TEXT", "VARCHAR2"),
  INTEGER("SMALLINT", "TINYINT", "NEDIUMINT", "BIGINT", "INT"),
  FLOAT("REAL", "DOUBLE PRECISION"),
  DECIMAL(ArgsResolverDecimalImpl.instance, "NUMERIC", "NUMBER"),
  TIME,
  DATE,
  TIMESTAMP,
  UNKNOWN;

  private ArgsResolver resolver;

  private Set<String> tags = new HashSet<>();

  private DataTypeName() {
    this.tags.add(name());
  }

  private DataTypeName(String... tags) {
    this();
    this.tags.addAll(List.of(tags));
  }

  private DataTypeName(ArgsResolver resolver, String... tags) {
    this(tags);
    this.resolver = resolver;
  }

  public static DataTypeName parse(String str) {
    String upperStr = str.toUpperCase();
    Optional<DataTypeName> name =
        Stream.of(values())
            .filter(dataTypeName -> dataTypeName.tags.contains(upperStr))
            .findFirst();

    if (name.isPresent()) {
      return name.get();
    }

    log.warn("Fail to parse {}", str);

    return UNKNOWN;
  }

  public void resolve(List<String> args, DataType dataType) {
    if (resolver == null) {
      return;
    }

    resolver.resolve(args, dataType);
  }

  static interface ArgsResolver {
    void resolve(List<String> args, DataType dataType);
  }

  static class ArgsResolverCharImpl implements ArgsResolver {

    static final ArgsResolver instance = new ArgsResolverCharImpl();

    @Override
    public void resolve(List<String> args, DataType dataType) {
      String[] argsArr = args.get(0).split(" ");

      dataType.setSize(Integer.parseInt(argsArr[0]));

      if (argsArr.length > 1) {
        dataType.setUnit(argsArr[1]);
      }
    }
  }

  static class ArgsResolverDecimalImpl implements ArgsResolver {

    static final ArgsResolver instance = new ArgsResolverDecimalImpl();

    @Override
    public void resolve(List<String> args, DataType dataType) {

      int totalDegit = Integer.parseInt(args.get(0));

      if (args.size() == 1) {
        dataType.setIntegerDigit(totalDegit);
      } else {
        int decimalDegit = Integer.parseInt(args.get(1));
        dataType.setIntegerDigit(totalDegit - decimalDegit);
        dataType.setDecimalDigit(decimalDegit);
      }
    }
  }
}
