package io.sitoolkit.rdg.core.application;

import static com.google.common.truth.Truth.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.ColumnPair;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.junit.Before;
import org.junit.Test;

public class SchemaAnalyzerTest {

  SchemaAnalyzer analyzer = new SchemaAnalyzer();
  Path workingDir;

  @Before
  public void setup() throws URISyntaxException {
    workingDir = Paths.get(SchemaAnalyzer.class.getClassLoader().getResource(".").toURI());
  }

  @Test
  public void testReadDynamicRelations() throws IOException, URISyntaxException {
    Path input = workingDir.resolve("create-and-select.sql");
    SchemaInfo answer = analyzer.read(input);

    List<SchemaDef> schemas = new ArrayList<>(answer.getSchemas());
    List<TableDef> tables =
        schemas.get(0).getTables().stream()
            .sorted(Comparator.comparing(TableDef::getName))
            .collect(Collectors.toList());

    Function<TableDef, List<ColumnDef>> sortColumnsFunc =
        table -> {
          return table.getColumns().stream()
              .sorted(Comparator.comparing(ColumnDef::getName))
              .collect(Collectors.toList());
        };
    List<ColumnDef> table1columns = new ArrayList<>(sortColumnsFunc.apply(tables.get(0)));
    List<ColumnDef> table2columns = new ArrayList<>(sortColumnsFunc.apply(tables.get(1)));
    List<ColumnDef> table3columns = new ArrayList<>(sortColumnsFunc.apply(tables.get(2)));

    List<RelationDef> table1relations4group1 = table1columns.get(0).getRelations();
    List<RelationDef> table1relations4group2 = table1columns.get(1).getRelations();
    List<RelationDef> table2relations4group1 = table2columns.get(1).getRelations();
    List<RelationDef> table3relations4group2 = table3columns.get(1).getRelations();

    assertThat(table1relations4group1).containsAnyIn(table2relations4group1);
    assertThat(table1relations4group2).containsAnyIn(table3relations4group2);

    assertThat(table1relations4group1).containsExactlyElementsIn(table1relations4group2);
  }

  @Test
  public void testReadStaticRelations() throws Exception {
    Path input = workingDir.resolve("create-table-with-foreign-key.sql");
    SchemaInfo schemaInfo = analyzer.read(input);
    assertReadStaticRelations(schemaInfo);
  }

  @Test
  public void testAnalyzeStaticRelations() {
    Path input = workingDir.resolve("create-table-with-foreign-key.sql");
    Path output = analyzer.analyze(input);
    SchemaInfo schemaInfo = SchemaInfo.read(output.getParent());
    assertReadStaticRelations(schemaInfo);
  }

  void assertReadStaticRelations(SchemaInfo schemaInfo) {
    SchemaDef schema = schemaInfo.findByName("").orElseThrow();
    List<RelationDef> relations = schema.getRelations();

    assertThat(relations.size(), is(2));

    ColumnPair pair_1_2 = relations.get(0).getColumnPairs().iterator().next();

    assertThat(pair_1_2.getLeft().getName(), is("COL_1_1"));
    assertThat(pair_1_2.getRight().getName(), is("COL_2_1"));

    ColumnPair pair_1_3 = relations.get(1).getColumnPairs().iterator().next();

    assertThat(pair_1_3.getLeft().getName(), is("COL_1_1"));
    assertThat(pair_1_3.getRight().getName(), is("COL_3_1"));

    ColumnDef col_1_1 = schema.findColumnByQualifiedName("TAB_1.COL_1_1").orElseThrow();
    assertThat(col_1_1.getRelations().get(0), is(relations.get(0)));
    ColumnDef col_2_1 = schema.findColumnByQualifiedName("TAB_2.COL_2_1").orElseThrow();
    assertThat(col_2_1.getRelations().get(0), is(relations.get(0)));

    ColumnDef col_3_1 = schema.findColumnByQualifiedName("TAB_3.COL_3_1").orElseThrow();
    assertThat(col_3_1.getRelations().get(0), is(equalTo(relations.get(1))));
  }

  @Test
  public void testReadAllSchemas() throws IOException {
    Path input = workingDir.resolve("multiple-schemas-create.sql");
    SchemaInfo schemaInfo = analyzer.read(input);
    assertReadAllSchemas(schemaInfo);
  }

  @Test
  public void testAnalyzeAllSchemas() {
    Path input = workingDir.resolve("multiple-schemas-create.sql");
    Path output = analyzer.analyze(input);
    SchemaInfo schemaInfo = SchemaInfo.read(output.getParent());
    assertReadAllSchemas(schemaInfo);
  }

  void assertReadAllSchemas(SchemaInfo schemaInfo) {
    SchemaDef schema1 = schemaInfo.findByName("SCHEMA1").orElseThrow();
    TableDef table1_1 = schema1.findTable("TABLE1").orElseThrow();
    assertThat(table1_1.getColumns().get(0).getName(), is("COLUMN1"));
    assertThat(table1_1.getColumns().get(1).getName(), is("COLUMN2"));

    SchemaDef schema2 = schemaInfo.findByName("SCHEMA2").orElseThrow();
    TableDef table1_2 = schema2.findTable("TABLE1").orElseThrow();
    assertThat(table1_2.getColumns().get(0).getName(), is("COLUMNA"));
    assertThat(table1_2.getColumns().get(1).getName(), is("COLUMNB"));
  }
}
