package io.sitoolkit.rdg.core.domain.generator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import io.sitoolkit.rdg.core.domain.generator.config.GeneratorConfig;
import io.sitoolkit.rdg.core.domain.generator.sequence.AbstractSequence;
import io.sitoolkit.rdg.core.domain.generator.sequence.MultipleSequentialValue;
import io.sitoolkit.rdg.core.domain.schema.ColumnDef;
import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratedValueStore {

  GeneratorConfig setting;

  /** List<RandomValueGroup>: 乱数Group　RelationDefに対し生成した乱数の集合 */
  private Map<RelationDef, List<RandomValueGroup>> generatedValueMap = new HashMap<>();

  private Map<RelationDef, Integer> requiredValueCountMap = new HashMap<>();

  private Map<List<ColumnDef>, AbstractSequence> registedSequence = new HashMap<>();

  private ColumnComparator comparator;

  public GeneratedValueStore(GeneratorConfig setting) {
    this.setting = setting;
    comparator = new ColumnComparator(setting);
  }

  public Optional<RandomValueRow> generateRow(
      List<ColumnDef> columns, List<ColumnDef> primaryKeys, int rowNum) {

    RandomValueRow valueRow = new RandomValueRow();

    MultipleSequentialValue leafSequence =
        (MultipleSequentialValue)
            registedSequence.computeIfAbsent(
                primaryKeys, key -> new MultipleSequentialValue(primaryKeys));

    leafSequence.nextVal();

    for (ColumnDef column : columns) {
      if (Objects.isNull(column.getRelations()) || column.getRelations().isEmpty()) {

        if (column.isPrimaryKey()) {
          valueRow.put(column, leafSequence.getSequenceByPkColumn(column).currentVal());

        } else {
          valueRow.put(column, RandomValueUtils.generate(column));
        }
      } else {
        valueRow.put(column, putIfAbsent(column, rowNum, leafSequence));
      }
    }

    return Optional.of(valueRow);
  }

  public String putIfAbsent(ColumnDef column, int rowNum, MultipleSequentialValue sequence) {
    List<RelationDef> relations = column.getRelations();

    // columnの全Relationに対し、乱数、および乱数Groupを生成してgeneratedValueMapに追加
    for (RelationDef relation : relations) {

      List<RandomValueGroup> generatedValueGroups =
          generatedValueMap.computeIfAbsent(relation, key -> new ArrayList<>());

      Integer requiredValueCount =
          requiredValueCountMap.computeIfAbsent(
              relation, key -> setting.getRequiredValueCount(key));

      if (generatedValueGroups.size() < requiredValueCount
          && generatedValueGroups.size() < rowNum) {

        RandomValueGroup generatedValueGroup = new RandomValueGroup();
        generatedValueGroups.add(generatedValueGroup);

        List<ColumnDef> distinctColumns = relation.getDistinctColumns();
        distinctColumns.sort(comparator::compare);
        String newValue = null;

        for (ColumnDef c : distinctColumns) {
          if (Objects.isNull(newValue)) {
            if (sequence.containsPkColumn(c)) {
              newValue = sequence.getSequenceByPkColumn(c).currentVal();
            } else {
              newValue = RandomValueUtils.generate(c);
            }
          }
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
