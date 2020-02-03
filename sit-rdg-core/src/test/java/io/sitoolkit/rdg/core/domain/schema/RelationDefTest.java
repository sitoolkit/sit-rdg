package io.sitoolkit.rdg.core.domain.schema;

import static com.google.common.truth.Truth.*;

import org.junit.Test;

public class RelationDefTest {

  @Test
  public void testHashCode() {
    ColumnDef one = new ColumnDef(null, "COLUMN_1", null, null, null, null, null);
    ColumnDef two = new ColumnDef(null, "COLUMN_2", null, null, null, null, null);
    ColumnDef three = new ColumnDef(null, "COLUMN_3", null, null, null, null, null);
    ColumnDef four = new ColumnDef(null, "COLUMN_4", null, null, null, null, null);
    RelationDef nodataRelation1 = new RelationDef();
    RelationDef nodataRelation2 = new RelationDef();
    RelationDef onePairRelation1 = new RelationDef();
    onePairRelation1.getColumnPairs().add(new ColumnPair(one, two));
    RelationDef onePairRelation2 = new RelationDef();
    onePairRelation2.getColumnPairs().add(new ColumnPair(one, two));
    RelationDef twoPairRelation1 = new RelationDef();
    twoPairRelation1.getColumnPairs().add(new ColumnPair(one, two));
    twoPairRelation1.getColumnPairs().add(new ColumnPair(three, four));
    RelationDef twoPairRelation2 = new RelationDef();
    twoPairRelation2.getColumnPairs().add(new ColumnPair(one, two));
    twoPairRelation2.getColumnPairs().add(new ColumnPair(three, four));

    assertThat(nodataRelation1.hashCode()).isEqualTo(nodataRelation2.hashCode());
    assertThat(nodataRelation1.equals(nodataRelation2)).isEqualTo(true);
    assertThat(onePairRelation1.hashCode()).isEqualTo(onePairRelation2.hashCode());
    assertThat(onePairRelation2.equals(onePairRelation2)).isEqualTo(true);
    assertThat(twoPairRelation1.hashCode()).isEqualTo(twoPairRelation2.hashCode());
    assertThat(twoPairRelation1.equals(twoPairRelation2)).isEqualTo(true);

    assertThat(nodataRelation1.hashCode()).isNotEqualTo(onePairRelation2.hashCode());
    assertThat(nodataRelation1.equals(onePairRelation2)).isEqualTo(false);
    assertThat(onePairRelation1.hashCode()).isNotEqualTo(twoPairRelation2.hashCode());
    assertThat(onePairRelation2.equals(twoPairRelation2)).isEqualTo(false);
  }
}
