package io.sitoolkit.rdg.core.domain.schema;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TableDef implements Comparable<TableDef> {

  @JsonBackReference private SchemaDef schema;

  @JsonIgnore private String schemaName;

  @JsonIgnore private String alias;

  @JsonProperty("fullyQualifiedName")
  private String fullyQualifiedName;

  @JsonProperty("tableName")
  private String name;

  @JsonProperty("columns")
  @JsonManagedReference
  private List<ColumnDef> columns;

  public Optional<String> getSchemaName() {
    if (Objects.isNull(schemaName)) {
      if (Objects.nonNull(schema) && Objects.nonNull(schema.getName())) {
        schemaName = schema.getName();
      }
    }
    return Optional.ofNullable(schemaName);
  }

  public List<ColumnDef> getColumns() {
    if (Objects.nonNull(columns)) {
      columns.stream().forEach(c -> c.setTable(this));
    }
    return columns;
  }

  public String getFullyQualifiedName() {
    if (Objects.isNull(fullyQualifiedName)) {
      StringJoiner joiner = new StringJoiner(".");
      if (getSchemaName().isPresent()) {
        joiner.add(getSchemaName().get());
      }
      joiner.add(getName());
      fullyQualifiedName = joiner.toString();
    }
    return fullyQualifiedName;
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

  @JsonIgnore
  public int getPkCnt() {
    int cnt = (int) getColumns().stream().filter(ColumnDef::isPrimaryKey).count();
    return cnt;
  }

  @Override
  public int compareTo(TableDef o) {

    if (getPkCnt() == o.getPkCnt()) {
      return getFullyQualifiedName().compareTo(o.getFullyQualifiedName());
    }

    if (getPkCnt() == 0) {
      return 1;
    }

    if (o.getPkCnt() == 0) {
      return -1;
    }

    return getPkCnt() - o.getPkCnt();
  }
}
