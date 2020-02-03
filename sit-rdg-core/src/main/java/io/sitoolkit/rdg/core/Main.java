package io.sitoolkit.rdg.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import io.sitoolkit.rdg.core.application.DataGenerator;
import io.sitoolkit.rdg.core.application.SchemaAnalyzer;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Main {

  static Option inputOpt =
      Option.builder("i")
          .argName("Input directory path")
          .desc("default is input")
          .longOpt("input")
          .required(false)
          .hasArg()
          .build();

  static Option outputOpt =
      Option.builder("o")
          .argName("Output directroy path")
          .desc("default is output")
          .longOpt("output")
          .required(false)
          .hasArg()
          .build();

  static Options options = new Options().addOption(inputOpt).addOption(outputOpt);

  SchemaAnalyzer schemaAnalyzer = new SchemaAnalyzer();

  DataGenerator dataGenerator = new DataGenerator();

  public static void main(String[] args) {
    System.exit(new Main().execute(args));
  }

  public int execute(String[] args) {

    CommandLineParser parser = new DefaultParser();
    HelpFormatter formatter = new HelpFormatter();

    CommandLine cmd;
    try {
      cmd = parser.parse(options, args);
      return execute(cmd);
    } catch (ParseException e) {
      formatter.printHelp("options", options);
      return 1;
    }
  }

  public int execute(CommandLine cmd) {

    try {

      Path input =
          Paths.get(cmd.getOptionValue(inputOpt.getLongOpt(), "input"))
              .toAbsolutePath()
              .normalize();

      Path output =
          Paths.get(cmd.getOptionValue(outputOpt.getLongOpt(), "output"))
              .toAbsolutePath()
              .normalize();

      if (cmd.getArgList().contains("read-sql")) {
        Path out = schemaAnalyzer.analyze(input);
        log.info("json: {}", out);
      }

      if (cmd.getArgList().contains("gen-data")) {
        List<Path> outputs = dataGenerator.generate(input, output);
        outputs.forEach(out -> log.info("csv: {}", out));
      }

      return 0;

    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }
}
