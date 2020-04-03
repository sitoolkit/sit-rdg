package io.sitoolkit.rdg.core.domain.schema;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.sitoolkit.rdg.core.infrastructure.JsonUtils;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaInfo {

  private static final String FILE_NAME = "schema.json";

  @JsonProperty("schemas")
  private Set<SchemaDef> schemas;

  @JsonIgnore
  public Optional<SchemaDef> findByName(String schemaName) {
    return schemas.stream().filter(schema -> schema.equalsName(schemaName)).findFirst();
  }

  @JsonIgnore
  public Optional<TableDef> findTable(String schemaName, String tableName) {
    Optional<SchemaDef> schemaOpt = findByName(schemaName);

    if (!schemaOpt.isPresent()) {
      return Optional.empty();
    }

    return schemaOpt.get().findTable(tableName);
  }

  public static SchemaInfo read(Path inDir) {
    SchemaInfo schemaInfo = JsonUtils.json2object(inDir.resolve(FILE_NAME), SchemaInfo.class);
    schemaInfo.afterDeserialize();
    ;
    return schemaInfo;
  }

  public Path write(Path outDir) {
    return JsonUtils.object2jsonFile(this, outDir.resolve(FILE_NAME));
  }

  public void afterDeserialize() {
    schemas.stream().forEach(SchemaDef::afterDeserialize);
  }
}
