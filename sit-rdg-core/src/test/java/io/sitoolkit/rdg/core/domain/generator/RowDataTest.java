package io.sitoolkit.rdg.core.domain.generator;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import java.util.HashMap;
import java.util.Map;
import org.junit.Test;

public class RowDataTest {

  @Test
  public void testEqualsAndHashCode() {
    RowData row_1 = new RowData();
    RowData row_2 = new RowData();

    TableDef tab_2 = new TableDef();
    tab_2.setName("tab_2");

    ColumnDef col_2_1 = new ColumnDef();
    col_2_1.setName("col_2_1");
    col_2_1.setTable(tab_2);

    ColumnDef col_2_2 = new ColumnDef();
    col_2_2.setName("col_2_2");
    col_2_2.setTable(tab_2);

    row_1.put(col_2_1, "1");
    row_1.put(col_2_2, "1");

    row_2.put(col_2_1, "2");
    row_2.put(col_2_2, "2");

    assertThat("equals", row_1.equals(row_2), is(false));
    // This assertion is important in this case.
    // row_1 and row_2 have different values each other so hashCode must return different value.
    // But if RowData.hashCode use HashMap.hashCode, row_1 and row_2 return same hashCode.
    // See also below static void main method.
    assertThat("hashCode", row_1.hashCode() == row_2.hashCode(), is(false));

    RowData row_3 = new RowData();

    row_3.put(col_2_1, "1");
    row_3.put(col_2_2, "1");

    assertThat("equals", row_1.equals(row_3), is(true));
    assertThat("hashCode", row_1.hashCode() == row_3.hashCode(), is(true));
  }

  public static void main(String[] args) {
    Map<String, String> map1 = new HashMap<>();
    Map<String, String> map2 = new HashMap<>();

    map1.put("tab_2.col_2_1", "1");
    map1.put("tab_2.col_2_2", "1");

    map2.put("tab_2.col_2_1", "2");
    map2.put("tab_2.col_2_2", "2");

    System.out.println(map1.hashCode());
    System.out.println(map2.hashCode());

    System.out.println(map1.equals(map2));
  }
}
