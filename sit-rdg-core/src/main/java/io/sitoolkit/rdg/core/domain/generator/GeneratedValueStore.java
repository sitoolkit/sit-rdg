package io.sitoolkit.rdg.core.domain.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratedValueStore {

  GeneratorConfig setting;

  /** List<RandomValueGroup>: 乱数Group　RelationDefに対し生成した乱数の集合 */
  private Map<RelationDef, List<RandomValueGroup>> generatedValueMap = new HashMap<>();

  private Map<RelationDef, Integer> requiredValueCountMap = new HashMap<>();

  private ConsistentValueGenerator generator = new ConsistentValueGenerator();

  public GeneratedValueStore(GeneratorConfig setting) {
    this.setting = setting;
  }

  public Optional<RandomValueRow> generateRow(List<ColumnDef> columns, int rowNum) {

    RandomValueRow valueRow = new RandomValueRow();

    for (ColumnDef column : columns) {

      // Relationが無い列の場合、乱数を生成してreturn
      if (Objects.isNull(column.getRelations()) || column.getRelations().isEmpty()) {
        valueRow.put(column, RandomValueUtils.generate(column));
      } else {
        valueRow.put(column, generateIfAbsent(generator, column, rowNum));
      }
    }

    return Optional.of(valueRow);
  }

  public String generateIfAbsent(ConsistentValueGenerator generator, ColumnDef column, int rowNum) {
    List<RelationDef> relations = column.getRelations();

    // columnの全Relationに対し、乱数、および乱数Groupを生成してgeneratedValueMapに追加
    for (RelationDef relation : relations) {

      List<RandomValueGroup> generatedValueGroups =
          generatedValueMap.computeIfAbsent(relation, key -> new ArrayList<>());

      Integer requiredValueCount =
          requiredValueCountMap.computeIfAbsent(
              relation, key -> setting.getRequiredValueCount(key));

      if (generatedValueGroups.size() < requiredValueCount) {

        RandomValueGroup generatedValueGroup = new RandomValueGroup();
        generatedValueGroups.add(generatedValueGroup);

        List<ColumnDef> distinctColumns = relation.getDistinctColumns();
        String newValue = null;
        for (ColumnDef c : distinctColumns) {
          newValue = Objects.isNull(newValue) ? generator.generate(c) : newValue;
          generatedValueGroup.setColumnValue(c, newValue);
          generatedValueGroup.generateEmptyColumnValue(relation);
        }
      }
    }

    RelationDef relation =
        relations
            .parallelStream()
            .filter(rel -> rel.getDistinctColumns().contains(column))
            .findAny()
            .get();
    List<RandomValueGroup> randomValueGroups = generatedValueMap.get(relation);
    RandomValueGroup randomValueGroup = randomValueGroups.get(rowNum - 1);

    return randomValueGroup.valueMap.get(column);
  }
}
