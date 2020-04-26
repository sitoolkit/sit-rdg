package io.sitoolkit.rdg.core.domain.generator;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public abstract class TableDataGenerator {

  private static final int RETRY_LIMIT = 10;

  @Getter(AccessLevel.PROTECTED)
  private final TableDef table;

  @Getter private List<RelationDataGenerator> generators = new ArrayList<>();

  @Getter(AccessLevel.PROTECTED)
  private final GeneratorConfig config;

  @Getter(AccessLevel.PROTECTED)
  private UniqueDataStore uniqueDataStore = new UniqueDataStore();

  public List<Object> generateLine() {

    int retryCount = 0;
    RetryException retryException = null;

    do {
      try {
        RowData rowData = generate();
        return rowData.toList(table.getColumns());
      } catch (RetryException e) {
        log.warn(
            "Failed row data generation and retry {} more times. cause: {}",
            RETRY_LIMIT - retryCount,
            e.getMessage());
        retryCount++;
        retryException = e;
      }
    } while (retryCount < RETRY_LIMIT);

    throw new IllegalStateException(retryException);
  }

  public abstract RowData generate();

  public void add(RelationDataGenerator generator) {
    generator.setUniqueDataStore(uniqueDataStore);
    generators.add(generator);
  }

  public String getTableName() {
    return table.getFullyQualifiedName();
  }

  public long getRequiredRowCount() {
    return config.getRowCount(table);
  }

  public List<Object> getHeader() {
    return table.getColumns().stream().map(ColumnDef::getName).collect(Collectors.toList());
  }

  protected List<UniqueConstraintDef> getUnrelatedUnieuqeConstraints() {
    List<UniqueConstraintDef> result = table.getUniqueConstraints();

    for (RelationDef relation : table.getRelations()) {
      result.removeAll(relation.getMainUniqueConstraints());
      result.removeAll(relation.getSubUniqueConstraints());
    }

    return result;
  }

  public void end() {
    generators.stream().forEach(RelationDataGenerator::end);
  }
}
