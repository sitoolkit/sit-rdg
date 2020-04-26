package io.sitoolkit.rdg.core.domain.check;

import io.sitoolkit.rdg.core.domain.schema.RelationDef;
import io.sitoolkit.rdg.core.domain.schema.TableDef;
import io.sitoolkit.rdg.core.domain.schema.UniqueConstraintDef;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.Getter;

public class CheckResult {
  @Getter private List<String> chekedFileNames = new ArrayList<>();

  @Getter private List<RelationDef> okRelations = new ArrayList<>();

  @Getter private List<RelationDef> ngRelations = new ArrayList<>();

  @Getter private List<UniqueConstraintDef> okUniques = new ArrayList<>();

  @Getter private List<UniqueConstraintDef> ngUniques = new ArrayList<>();

  public Path addFile(Path file) {
    chekedFileNames.add(file.getFileName().toString());
    return file;
  }

  public List<Object> getErrorList() {
    List<Object> errorList = new ArrayList<>();
    errorList.addAll(ngRelations);
    errorList.addAll(ngUniques);
    return errorList;
  }

  public boolean hasError() {
    return !(ngRelations.isEmpty() && ngUniques.isEmpty());
  }

  public String getErrorMessage() {
    List<String> message = new ArrayList<>();

    Set<TableDef> okTables = rels2tab(okRelations);
    Set<TableDef> ngTables = rels2tab(ngRelations);

    message.add("");

    if (!ngTables.isEmpty()) {
      message.add("NG Tables: " + tabs2str(ngTables));
    }

    for (RelationDef relation : ngRelations) {
      message.add("NG " + relation);
    }

    for (UniqueConstraintDef unique : ngUniques) {
      message.add("NG " + unique.getTable().getName() + " " + unique);
    }

    message.add("=== Summary ===");
    message.add(
        "OK tables: "
            + okTables.size()
            + ", relations: "
            + okRelations.size()
            + ", unique constraints:"
            + okUniques.size());
    message.add(
        "NG tables: "
            + ngTables.size()
            + ", relations: "
            + ngRelations.size()
            + ", unique constraints: "
            + ngUniques.size());

    return message.stream().collect(Collectors.joining(System.lineSeparator()));
  }

  Set<TableDef> rels2tab(List<RelationDef> rels) {
    return rels.stream()
        .map(RelationDef::getTables)
        .flatMap(Collection::stream)
        .collect(Collectors.toSet());
  }

  String tabs2str(Collection<TableDef> tables) {
    return tables.stream().map(TableDef::getName).collect(Collectors.joining(","));
  }

  String rel2str(Collection<RelationDef> relations) {
    return relations.stream()
        .map(Object::toString)
        .collect(Collectors.joining(System.lineSeparator()));
  }
}
