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

public class GeneratedValueStore {

  private GeneratorConfig setting;

  /** List<RandomValueGroup>: 乱数Group　RelationDefに対し生成した乱数の集合 */
  private Map<RelationDef, List<RandomValueGroup>> generatedValueMap = new HashMap<>();

  private Map<ColumnDef, Integer> requiredValueCountMap = new HashMap<>();

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

    RelationDef relation =
        relations
            .parallelStream()
            .filter(rel -> rel.getDistinctColumns().contains(column))
            .findAny()
            .get();

    List<RandomValueGroup> generatedValueGroups =
        generatedValueMap.computeIfAbsent(relation, key -> new ArrayList<>());

    int requiredValueCount =
        requiredValueCountMap
            .computeIfAbsent(column, key -> setting.getRequiredValueCount(key))
            .intValue();

    int originRowNum = (rowNum - 1) / requiredValueCount + 1;

    if (generatedValueGroups.size() < originRowNum) {
      List<ColumnDef> distinctColumns = relation.getDistinctColumns();
      distinctColumns.sort(comparator::compare);
      RandomValueGroup origin = new RandomValueGroup();
      generatedValueGroups.add(origin);

      for (ColumnDef c : distinctColumns) {
        origin.setColumnValue(c, generateValue(sequence, column));
      }
    }

    return pickupGeneratedValue(relation, column, originRowNum);
  }

  public String generateValue(MultipleSequentialValue sequence, ColumnDef column) {
    if (column.isPrimaryKey()) {
      if (sequence.containsPkColumn(column)) {
        return sequence.getSequenceByPkColumn(column).currentVal();
      } else {
        return null;
      }
    }
    return RandomValueUtils.generate(column);
  }

  public String pickupGeneratedValue(RelationDef relation, ColumnDef column, int rowNum) {

    List<RandomValueGroup> randomValueGroups = generatedValueMap.get(relation);
    RandomValueGroup randomValueGroup = randomValueGroups.get(rowNum - 1);

    return randomValueGroup.valueMap.get(column);
  }
}
