package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaInfo {

  @JsonProperty("schemas")
  private Set<SchemaDef> schemas;

  @JsonIgnore
  public Optional<SchemaDef> findByName(String schemaName) {
    return schemas.stream()
        .filter(schema -> StringUtils.equalsIgnoreCase(schema.getName(), schemaName))
        .findFirst();
  }

  @JsonIgnore
  public Optional<TableDef> findTable(String schemaName, String tableName) {
    Optional<SchemaDef> schemaOpt = findByName(schemaName);

    if (!schemaOpt.isPresent()) {
      return Optional.empty();
    }

    return schemaOpt.get().findTable(tableName);
  }
}
