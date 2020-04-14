package io.sitoolkit.rdg.core.application;


import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfigReader;

public class DataGeneratorTablePriorityImplTest {
  DataGeneratorTablePriorityImpl generator = new DataGeneratorTablePriorityImpl();

  GeneratorConfigReader reader = new GeneratorConfigReader();

  // @Test
  // public void test() throws IOException {
  //   List<ColumnDef> columns =
  //       List.of(
  //           ColumnDef.builder()
  //               .name("col_default")
  //               .dataType(DataType.builder().name(DataTypeName.CHAR).size(3).build())
  //               .build(),
  //           ColumnDef.builder().name("col_seq").build(),
  //           ColumnDef.builder().name("col_choice").build(),
  //           ColumnDef.builder().name("col_range").build());
  //   TableDef tableDef = TableDef.builder().name("tab_1").columns(columns).build();

  //   int rowCount = 10;
  //   List<Path> outDir = List.of(Path.of("target"));
  //   String fileName = "tab_1.csv";

  //   GeneratorConfig config =
  //       reader.readFile(TestResourceUtils.res2path(this, "generator-config.json"));
  //   GeneratedValueStore store = new GeneratedValueStore(config);

  //   List<Path> out = generator.generate(tableDef, rowCount, outDir, fileName, store);

  //   List<CSVRecord> records =
  //       CSVParser.parse(out.get(0), Charset.defaultCharset(), CSVFormat.DEFAULT).getRecords();

  //   assertThat(records.size(), is(rowCount + 1));
  // }
}
