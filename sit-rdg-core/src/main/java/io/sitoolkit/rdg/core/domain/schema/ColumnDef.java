package io.sitoolkit.rdg.core.domain.schema;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ColumnDef implements Comparable<ColumnDef> {

  private static final long serialVersionUID = 1L;

  @JsonBackReference
  @JsonProperty("table")
  private TableDef table;

  @JsonProperty("columnName")
  private String name;

  @JsonProperty("fullyQualifiedName")
  private String fullyQualifiedName;

  @JsonProperty("dataType")
  private DataType dataType;

  @JsonProperty("argumentsStringList")
  private List<String> args;

  @JsonProperty("constraints")
  private List<ConstraintAttribute> constraints;

  @JsonProperty("relations")
  private List<RelationDef> relations;

  public static ColumnDef shallowCopyExcludeRelations(ColumnDef columnDef) {
    ColumnDef newColumn = new ColumnDef();
    newColumn.setTable(columnDef.getTable());
    newColumn.setFullyQualifiedName(columnDef.getFullyQualifiedName());
    newColumn.setName(columnDef.getName());
    newColumn.setArgs(columnDef.getArgs());
    newColumn.setDataType(columnDef.getDataType());
    newColumn.setConstraints(columnDef.getConstraints());
    return newColumn;
  }

  public String getFullyQualifiedName() {
    if (Objects.isNull(fullyQualifiedName) || Objects.nonNull(table)) {
      StringJoiner joiner = new StringJoiner(".");
      if (Objects.nonNull(table)) {
        if (table.getSchemaName().isPresent()) {
          joiner.add(table.getSchemaName().get());
        }
        String tableName = table.getName();
        joiner.add(tableName);
      }
      joiner.add(name);
      fullyQualifiedName = joiner.toString();
    }
    return fullyQualifiedName;
  }

  @JsonIgnore
  @Getter(lazy = true)
  private final Integer integerDigit = calcIntegerDigit();

  private int calcIntegerDigit() {
    if (Objects.nonNull(args) && args.size() > 0) {
      Integer integerDigit = Integer.parseInt(args.get(0));
      Integer decimalDigit = getDecimalDigit();
      if (decimalDigit == 0) {
        return integerDigit;
      }
      return integerDigit - decimalDigit;
    }
    return 0;
  }

  @JsonIgnore
  @Getter(lazy = true)
  private final Integer decimalDigit = calcDecimalDigit();

  private int calcDecimalDigit() {
    if (Objects.nonNull(args) && args.size() > 1) {
      return Integer.parseInt(args.get(1));
    }
    return 0;
  }

  @JsonIgnore
  public DataType meansDataType() {
    if (Stream.of(DataType.CHAR, DataType.VARCHAR, DataType.VARCHAR2)
            .anyMatch(Predicate.isEqual(getDataType()))
        && StringUtils.endsWithAny(getName().toUpperCase(), "DATE", "YM")) {
      return DataType.MEANS_DATE;
    }
    if (Stream.of(DataType.VARCHAR, DataType.VARCHAR2).anyMatch(Predicate.isEqual(getDataType()))
        && StringUtils.endsWithAny(getName().toUpperCase(), "ID")) {
      return DataType.MEANS_ID;
    }
    if (Stream.of(DataType.VARCHAR, DataType.VARCHAR2).anyMatch(Predicate.isEqual(getDataType()))
        && StringUtils.endsWithAny(getName().toUpperCase(), "AMT", "INCOME")) {
      return DataType.MEANS_DECIMAL;
    }
    if (Stream.of(DataType.VARCHAR, DataType.VARCHAR2).anyMatch(Predicate.isEqual(getDataType()))
        && StringUtils.endsWithAny(getName().toUpperCase(), "CODE")) {
      return DataType.NUMBER;
    }
    return getDataType();
  }

  @JsonIgnore
  public boolean isPrimaryKey() {
    return Objects.nonNull(constraints) && constraints.contains(ConstraintAttribute.PRIMARY_KEY);
  }

  @Override
  public int hashCode() {
    return getFullyQualifiedName().hashCode();
  }

  @Override
  public boolean equals(Object o) {
    return StringUtils.equals(toString(), o.toString());
  }

  @Override
  public String toString() {
    return getFullyQualifiedName();
  }

  @Override
  public int compareTo(ColumnDef o) {
    return getFullyQualifiedName().compareTo(o.getFullyQualifiedName());
  }
}
