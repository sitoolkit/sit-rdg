package io.sitoolkit.rdg.core.domain.schema;

import java.util.Comparator;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 当該オブジェクトが「等価である」と評価される条件は、 「互いにテーブル名、列名が等価であるColumnDefオブジェクトを持つこと」である。
 *
 * <p>この挙動は、 「フィールドをSortedSet columnsのみ持ち」、「@EqualsAndHashcodeを宣言する」 ことにより実現している。
 */
@EqualsAndHashCode
@NoArgsConstructor
public class ColumnPair {

  @Getter
  private SortedSet<ColumnDef> columns =
      new TreeSet<>(Comparator.comparing(ColumnDef::getFullyQualifiedName));

  public ColumnPair(ColumnDef left, ColumnDef right) {
    List.of(left, right).stream().forEach(columns::add);
  }
}
