package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 当該オブジェクトが「等価である」と評価される条件は、 「互いにテーブル名、列名が等価であるColumnDefオブジェクトを持つこと」である。
 *
 * <p>この挙動は、 「フィールドをSortedSet columnsのみ持ち」、「@EqualsAndHashcodeを宣言する」 ことにより実現している。
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ColumnPair {

  @EqualsAndHashCode.Include @Getter private List<ColumnDef> columns = new ArrayList<>();

  public ColumnPair(ColumnDef main, ColumnDef sub) {
    List.of(main, sub).stream().forEach(columns::add);
  }

  @JsonIgnore
  public ColumnDef getMain() {
    return columns.get(0);
  }

  @JsonIgnore
  public ColumnDef getSub() {
    return columns.get(1);
  }

  @JsonCreator
  public ColumnPair(@JsonProperty("main") String main, @JsonProperty("sub") String sub) {

    this(
        ColumnDef.builder().fullyQualifiedName(main).build(),
        ColumnDef.builder().fullyQualifiedName(sub).build());
  }

  @JsonValue
  public Map<String, String> toJson() {
    Map<String, String> json = new HashMap<>();
    json.put("main", getMain().getFullyQualifiedName());
    json.put("sub", getSub().getFullyQualifiedName());
    return json;
  }

  public void reset(ColumnDef main, ColumnDef sub) {
    columns.clear();
    columns.add(main);
    columns.add(sub);
  }

  @Override
  public String toString() {
    return "main:"
        + getMain().getFullyQualifiedName()
        + ", sub: "
        + getSub().getFullyQualifiedName();
  }
}
