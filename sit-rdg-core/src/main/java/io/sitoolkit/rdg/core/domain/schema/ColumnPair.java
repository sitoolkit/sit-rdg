package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * 当該オブジェクトが「等価である」と評価される条件は、 「互いにテーブル名、列名が等価であるColumnDefオブジェクトを持つこと」である。
 *
 * <p>この挙動は、 「フィールドをSortedSet columnsのみ持ち」、「@EqualsAndHashcodeを宣言する」 ことにより実現している。
 */
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(doNotUseGetters = true, onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ColumnPair {

  @JsonProperty("main")
  @ToString.Include(name = "main")
  @EqualsAndHashCode.Include
  private String mainColName;

  @JsonProperty("sub")
  @ToString.Include(name = "sub")
  @EqualsAndHashCode.Include
  private String subColName;

  @JsonBackReference private RelationDef relation;

  @Getter(lazy = true)
  @JsonIgnore
  private final ColumnDef main = relation.getSchema().findColumnByQualifiedName(mainColName).get();

  @Getter(lazy = true)
  @JsonIgnore
  private final ColumnDef sub = relation.getSchema().findColumnByQualifiedName(subColName).get();

  @Getter(lazy = true)
  @JsonIgnore
  private final List<ColumnDef> columns = List.of(getMain(), getSub());

  public ColumnPair(String mainColName, String subColName) {
    this.mainColName = mainColName;
    this.subColName = subColName;
  }
}
