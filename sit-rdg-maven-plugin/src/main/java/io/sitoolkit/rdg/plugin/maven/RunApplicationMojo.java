package io.sitoolkit.rdg.plugin.maven;

import io.sitoolkit.rdg.core.Main;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

@Mojo(name = "run")
public class RunApplicationMojo extends AbstractMojo {

  @Parameter(defaultValue = "true")
  private boolean readSql;

  @Parameter(defaultValue = "true")
  private boolean genData;

  @Parameter private String input;

  @Parameter private String output;

  @Override
  public void execute() throws MojoExecutionException {
    int exitCode = new Main().execute(getArgsAsArray());

    if (exitCode != 0) {
      throw new MojoExecutionException("Execution failure. See above statck trace.");
    }
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
