package io.sitoolkit.rdg.core.application;

import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.infrastructure.TestResourceUtils;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TestName;

public class SchemaAnalyzerTest {

  SchemaAnalyzer analyzer = new SchemaAnalyzer();
  Path workDir;

  @Rule public TestName testName = new TestName();

  @Before
  public void setup() throws IOException {
    workDir = Path.of("target", getClass().getSimpleName(), testName.getMethodName());
    FileUtils.deleteDirectory(workDir.toFile());
  }

  @Test
  public void testAnalyzeStaticRelations() {
    Path dstDir = TestResourceUtils.copyResDir(this, testName.getMethodName());
    Path outFile = analyzer.analyze(dstDir);
    SchemaInfo schemaInfo = SchemaInfo.read(outFile.getParent());
    assertReadStaticRelations(schemaInfo);
  }

  void assertReadStaticRelations(SchemaInfo schemaInfo) {
    SchemaDef schema = schemaInfo.findByName("").orElseThrow();

    TableDef tab_1 = schema.findTable("tab_1").orElseThrow();
    TableDef tab_2 = schema.findTable("tab_2").orElseThrow();
    TableDef tab_3 = schema.findTable("tab_3").orElseThrow();
    TableDef tab_4 = schema.findTable("tab_4").orElseThrow();

    assertThat("tab_1 is independent", tab_1.isDependent(), is(false));
    assertThat("tab_2 is dependent", tab_2.isDependent(), is(true));
    assertThat("tab_3 is dependent", tab_3.isDependent(), is(true));
    assertThat("tab_4 is dependent", tab_4.isDependent(), is(true));

    assertThat(tab_2.getAncestors().get(0).getName(), is("tab_1"));
    assertThat(tab_3.getAncestors().get(0).getName(), is("tab_2"));
    assertThat(tab_3.getAncestors().get(1).getName(), is("tab_1"));

    assertThat("tab_2 depends on tab_1", tab_2.dependsOn(tab_1), is(true));
    assertThat("tab_3 depends on tab_2", tab_3.dependsOn(tab_2), is(true));
    assertThat("tab_4 depends on tab_1", tab_4.dependsOn(tab_1), is(true));

    List<RelationDef> relations = schema.getRelations();

    assertThat(relations.size(), is(3));

    RelationDef rel_1_2 = relations.get(0);
    RelationDef rel_2_3 = relations.get(1);

    ColumnPair pair_1_2 = rel_1_2.getColumnPairs().iterator().next();

    assertThat(pair_1_2.getMain().getName(), is("col_1_1"));
    assertThat(pair_1_2.getSub().getName(), is("col_2_1"));

    List<ColumnPair> pair_2_3 = new ArrayList<>(rel_2_3.getColumnPairs());

    assertThat(pair_2_3.get(0).getMain().getName(), is("col_2_1"));
    assertThat(pair_2_3.get(0).getSub().getName(), is("col_3_1"));

    assertThat(pair_2_3.get(1).getMain().getName(), is("col_2_2"));
    assertThat(pair_2_3.get(1).getSub().getName(), is("col_3_2"));
  }

  @Test
  public void testAnalyzeAllSchemas() {
    Path dstDir = TestResourceUtils.copyResDir(this, testName.getMethodName());
    Path outFile = analyzer.analyze(dstDir);
    SchemaInfo schemaInfo = SchemaInfo.read(outFile.getParent());
    assertReadAllSchemas(schemaInfo);
  }

  void assertReadAllSchemas(SchemaInfo schemaInfo) {
    SchemaDef schema1 = schemaInfo.findByName("SCHEMA1").orElseThrow();
    TableDef table1_1 = schema1.findTable("tabLE1").orElseThrow();
    assertThat(table1_1.getColumns().get(0).getName(), is("COLUMN1"));
    assertThat(table1_1.getColumns().get(1).getName(), is("COLUMN2"));

    SchemaDef schema2 = schemaInfo.findByName("SCHEMA2").orElseThrow();
    TableDef table1_2 = schema2.findTable("tabLE1").orElseThrow();
    assertThat(table1_2.getColumns().get(0).getName(), is("COLUMNA"));
    assertThat(table1_2.getColumns().get(1).getName(), is("COLUMNB"));
  }

  @Test
  public void testUniqueConstraint() {
    Path input = TestResourceUtils.copyResDir(this, testName.getMethodName());
    SchemaInfo schemaInfo = analyzer.read(input);
    assertUniqueConstraint(schemaInfo);
    schemaInfo.write(input.getParent());
  }

  void assertUniqueConstraint(SchemaInfo schemaInfo) {

    TableDef tab_1 = schemaInfo.findTable("", "tab_1").orElseThrow();
    assertThat(tab_1.getUniqueConstraints().get(0).getColumnNames(), is(List.of("col_1_1")));

    TableDef tab_2 = schemaInfo.findTable("", "tab_2").orElseThrow();
    assertThat(tab_2.getUniqueConstraints().get(0).getColumnNames(), is(List.of("col_2_1")));

    TableDef tab_3 = schemaInfo.findTable("", "tab_3").orElseThrow();
    assertThat(tab_3.getUniqueConstraints().get(0).getColumnNames(), is(List.of("col_3_1")));

    TableDef tab_4 = schemaInfo.findTable("", "tab_4").orElseThrow();
    assertThat(tab_4.getUniqueConstraints().get(0).getColumnNames(), is(List.of("col_4_1")));

    TableDef tab_5 = schemaInfo.findTable("", "tab_5").orElseThrow();
    assertThat(tab_5.getUniqueConstraints().get(0).getColumnNames(), is(List.of("col_5_1")));
    assertThat(tab_5.getUniqueConstraints().get(1).getColumnNames(), is(List.of("col_5_2")));
  }
}
