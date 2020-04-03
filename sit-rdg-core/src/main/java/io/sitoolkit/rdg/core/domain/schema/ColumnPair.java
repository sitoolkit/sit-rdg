package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 当該オブジェクトが「等価である」と評価される条件は、 「互いにテーブル名、列名が等価であるColumnDefオブジェクトを持つこと」である。
 *
 * <p>この挙動は、 「フィールドをSortedSet columnsのみ持ち」、「@EqualsAndHashcodeを宣言する」 ことにより実現している。
 */
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ColumnPair {

  @Getter
  private SortedSet<ColumnDef> columns =
      new TreeSet<>(Comparator.comparing(ColumnDef::getFullyQualifiedName));

  public ColumnPair(ColumnDef left, ColumnDef right) {
    List.of(left, right).stream().forEach(columns::add);
  }

  @JsonCreator
  public ColumnPair(@JsonProperty("left") String left, @JsonProperty("right") String right) {
    this(
        ColumnDef.builder().fullyQualifiedName(left).build(),
        ColumnDef.builder().fullyQualifiedName(right).build());
  }

  @JsonIgnore
  public ColumnDef getLeft() {
    return columns.first();
  }

  @JsonIgnore
  public ColumnDef getRight() {
    return columns.last();
  }

  @JsonValue
  public Map<String, String> toJson() {
    Map<String, String> json = new HashMap<>();
    json.put("left", getLeft().getFullyQualifiedName());
    json.put("right", getRight().getFullyQualifiedName());
    return json;
  }
}
