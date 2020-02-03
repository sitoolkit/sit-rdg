package io.sitoolkit.rdg.core.domain.schema;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SchemaInfo {

  @JsonProperty("schemas")
  public Set<SchemaDef> schemas;
}
