package io.sitoolkit.rdg.core.infrastructure;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.csv.CSVRecord;

@Slf4j
@AllArgsConstructor
public class CsvData {

  private List<CSVRecord> records;

  @Getter private String fileName;

  public boolean containsAll(String myCol, CsvData another, String anotherCol) {
    return containsAll(List.of(myCol), another, List.of(anotherCol));
  }

  @SuppressWarnings("unchecked")
  public boolean containsAll(List<String> myCols, CsvData another, List<String> anotherCols) {

    log.debug("Comparing {} vs {}", myCols, anotherCols);

    List<List<String>> myRecords = CsvUtils.selectCols(records, myCols, ArrayList.class);
    List<List<String>> anotherRecords =
        CsvUtils.selectCols(another.records, anotherCols, ArrayList.class);

    boolean containsAll = true;

    for (List<String> anotherRecord : anotherRecords) {
      if (!myRecords.contains(anotherRecord)) {
        log.error("No parent data: {} = {}", anotherCols, anotherRecord);
        containsAll = false;
      }
    }

    return containsAll;
  }

  @SuppressWarnings("unchecked")
  public boolean isUnique(List<String> cols) {
    return CsvUtils.selectCols(records, cols, HashSet.class).size() == records.size();
  }

  public String get(int row, String col) {
    return records.get(row).get(col);
  }
}
