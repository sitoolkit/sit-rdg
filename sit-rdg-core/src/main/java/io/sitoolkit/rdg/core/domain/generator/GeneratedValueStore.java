package io.sitoolkit.rdg.core.domain.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

import org.apache.commons.lang3.RandomUtils;

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

  private Set<RandomValueRow> generatedRows = new HashSet<>();

  public void clearGeneratedRowsCache() {
    generatedRows.clear();
  }

  public GeneratedValueStore(GeneratorConfig setting) {
    this.setting = setting;
  }

  public Optional<RandomValueRow> generateRow(List<ColumnDef> columns) {

    for (; ; ) {

      RandomValueRow valueRow = new RandomValueRow();

      for (ColumnDef column : columns) {
        String value = generateIfAbsent(column);
        valueRow.put(column, value);
      }

      if (!generatedRows.contains(valueRow)) {
        generatedRows.add(valueRow);
        return Optional.of(valueRow);
      }

      log.info(
          "Duplicate primrary key:{} of {}",
          valueRow.getPrimaryKeyValues(),
          columns.stream().findAny().get().getTable().getFullyQualifiedName());
    }
  }

  public String generateIfAbsent(ColumnDef column) {
    List<RelationDef> relations = column.getRelations();

    // Relationが無い列の場合、乱数を生成してreturn
    if (Objects.isNull(relations) || relations.isEmpty()) {
      return RandomValueUtils.generate(column);
    }

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
          newValue = Objects.isNull(newValue) ? RandomValueUtils.generate(c) : newValue;
          generatedValueGroup.setColumnValue(c, newValue);
          generatedValueGroup.generateEmptyColumnValue(relation);
        }
      }
    }

    // 生成済みの乱数の中からランダムに値を取得してreturn
    RelationDef relation =
        relations
            .parallelStream()
            .filter(r -> r.getDistinctColumns().contains(column))
            .findAny()
            .get();
    List<RandomValueGroup> randomValueGroups = generatedValueMap.get(relation);
    RandomValueGroup randomValueGroup =
        randomValueGroups.get(RandomUtils.nextInt(0, randomValueGroups.size()));

    String generatedValue = randomValueGroup.valueMap.get(column);

    return generatedValue;
  }
}
