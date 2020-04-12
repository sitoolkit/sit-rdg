package io.sitoolkit.rdg.core.infrastructure;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.apache.commons.csv.CSVRecord;

@AllArgsConstructor
public class CsvData {

  private List<CSVRecord> records;

  @Getter private String fileName;

  public boolean containsAll(String myCol, CsvData another, String anotherCol) {
    return containsAll(List.of(myCol), another, List.of(anotherCol));
  }

  public boolean containsAll(List<String> myCols, CsvData another, List<String> anotherCols) {
    List<String> myRecords = CsvUtils.selectCols(records, myCols);

    List<String> anotherRecords = CsvUtils.selectCols(another.records, anotherCols);

    return myRecords.containsAll(anotherRecords);
  }
}
