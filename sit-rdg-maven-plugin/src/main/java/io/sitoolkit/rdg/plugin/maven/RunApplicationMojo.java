package io.sitoolkit.rdg.plugin.maven;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import io.sitoolkit.rdg.core.Main;

@Mojo(name = "run")
public class RunApplicationMojo extends AbstractMojo {

  private static final String READ_SQL_OPTION = "readSql";
  private static final String GEN_DATA_OPTION = "genData";
  private static final String INPUT_OPTION = "input";
  private static final String OUTPUT_OPTION = "output";

  @Parameter(defaultValue = "true")
  private boolean readSql;

  @Parameter(defaultValue = "true")
  private boolean genData;

  @Parameter private String input;

  @Parameter private String output;

  @Override
  public void execute() {
    getLog().info("readSql:" + readSql);
    getLog().info("genData:" + genData);
    getLog().info("input:" + input);
    getLog().info("output:" + output);
    new Main().execute(getArgsAsArray());
  }

  private String[] getArgsAsArray() {

    List<String> args = new ArrayList<>();

    if (readSql) {
      args.add("read-sql");
    }

    if (genData) {
      args.add("gen-data");
    }

    if (Objects.nonNull(input)) {
      args.add("--input");
      args.add(input);
    }

    if (Objects.nonNull(output)) {
      args.add("--output");
      args.add(output);
    }

    return args.toArray(String[]::new);
  }
}
