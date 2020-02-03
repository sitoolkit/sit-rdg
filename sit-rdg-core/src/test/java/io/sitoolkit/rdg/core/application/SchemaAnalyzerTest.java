package io.sitoolkit.rdg.core.application;

import static com.google.common.truth.Truth.assertThat;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;

import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaDef;
import io.sitoolkit.rdg.core.domain.schema.SchemaInfo;
import io.sitoolkit.rdg.core.domain.schema.TableDef;

public class SchemaAnalyzerTest {

  SchemaAnalyzer analyzer = new SchemaAnalyzer();
  Path workingDir;

  @Before
  public void setup() throws URISyntaxException {
    workingDir = Paths.get(SchemaAnalyzer.class.getClassLoader().getResource(".").toURI());
  }

  @Test
  public void shouldReadRelations() throws IOException, URISyntaxException {
    Path input = workingDir.resolve("create-and-select.sql");
    SchemaInfo answer = analyzer.read(input);

    List<SchemaDef> schemas = new ArrayList<>(answer.getSchemas());
    List<TableDef> tables =
        schemas
            .get(0)
            .getTables()
            .stream()
            .sorted(Comparator.comparing(TableDef::getName))
            .collect(Collectors.toList());

    Function<TableDef, List<ColumnDef>> sortColumnsFunc =
        table -> {
          return table
              .getColumns()
              .stream()
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

  //  @Test
  public void shouldReadAllSchemas() throws IOException {
    Path input = workingDir.resolve("multiple-schemas-create.sql");

    TableDef expctTable1onSchema1 = new TableDef();
    expctTable1onSchema1.setName("SCHEMA1.TABLE1");
    List<ColumnDef> expctColumnSetOnSchema1 =
        createColumnSetAbout(expctTable1onSchema1).apply(List.of("COLUMN1", "COLUMN2"));
    expctTable1onSchema1.setColumns(expctColumnSetOnSchema1);
    SortedSet<TableDef> set1 = new TreeSet();
    set1.add(expctTable1onSchema1);
    SchemaDef expctSchema1 = new SchemaDef("SCHEMA1", set1);

    TableDef expctTable1onSchema2 = new TableDef();
    expctTable1onSchema2.setName("SCHEMA2.TABLE1");
    List<ColumnDef> expctColumnSetOnSchema2 =
        createColumnSetAbout(expctTable1onSchema2).apply(List.of("COLUMNA", "COLUMNB"));
    expctTable1onSchema2.setColumns(expctColumnSetOnSchema2);
    SortedSet<TableDef> set2 = new TreeSet();
    set2.add(expctTable1onSchema2);
    SchemaDef expctSchema2 = new SchemaDef("SCHEMA2", set2);

    SchemaInfo answer = analyzer.read(input);

    List<SchemaDef> resultSchemas =
        new ArrayList<>(
            answer
                .getSchemas()
                .stream()
                .sorted(Comparator.comparing(SchemaDef::getName))
                .collect(Collectors.toList()));
    List<TableDef> resultTablesOnSchema1 = new ArrayList<>(resultSchemas.get(0).getTables());
    List<TableDef> resultTablesOnSchema2 = new ArrayList<>(resultSchemas.get(1).getTables());
    List<ColumnDef> resultColumnsOnSchema1 = resultTablesOnSchema1.get(0).getColumns();
    List<ColumnDef> resultColumnsOnSchema2 = resultTablesOnSchema2.get(0).getColumns();

    assertThat(resultSchemas).containsExactlyElementsIn(List.of(expctSchema1, expctSchema2));
    assertThat(resultTablesOnSchema1).containsExactlyElementsIn(Set.of(expctTable1onSchema1));
    assertThat(resultTablesOnSchema2).containsExactlyElementsIn(Set.of(expctTable1onSchema2));
    assertThat(resultColumnsOnSchema1).containsExactlyElementsIn(expctColumnSetOnSchema1);
    assertThat(resultColumnsOnSchema2).containsExactlyElementsIn(expctColumnSetOnSchema2);
  }

  private Function<List<String>, List<ColumnDef>> createColumnSetAbout(TableDef table) {
    return columnNames -> {
      return columnNames
          .stream()
          .map(name -> new ColumnDef(table, name, null, null, null, null, null))
          .collect(Collectors.toList());
    };
  }
}
