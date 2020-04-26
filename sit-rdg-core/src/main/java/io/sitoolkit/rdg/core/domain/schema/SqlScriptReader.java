package io.sitoolkit.rdg.core.domain.schema;


/** SqlScriptReader */
public interface SqlScriptReader {

  void read(String sqlText);

  SchemaInfo getSchemaInfo();
}
