package io.sitoolkit.rdg.core.domain.schema;

import java.nio.file.Path;

/** SqlScriptReader */
public interface SqlScriptReader {

  void read(Path sqlScriptFile);

  SchemaInfo getSchemaInfo();
}
