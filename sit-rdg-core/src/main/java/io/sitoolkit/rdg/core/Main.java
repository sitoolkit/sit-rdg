package io.sitoolkit.rdg.core;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.lang3.math.NumberUtils;

import io.sitoolkit.rdg.core.application.DataGenerator;
import io.sitoolkit.rdg.core.application.SchemaAnalyzer;
import io.sitoolkit.rdg.core.infrastructure.BufferedAsyncCsvWriter;
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

  static Option bufferSizeOpt =
      Option.builder("b")
          .argName("Buffre size of csv row count")
          .desc("default is 1000")
          .longOpt("bufferSize")
          .required(false)
          .hasArg()
          .build();

  static Option flushWaitAlertSecOpt =
      Option.builder("fwa")
          .argName("Threshold to alert of wait span for csv writing")
          .desc("default is 10 (sec)")
          .longOpt("flushWaitAlertSec")
          .required(false)
          .hasArg()
          .build();

  static Options options =
      new Options()
          .addOption(inputOpt)
          .addOption(outputOpt)
          .addOption(bufferSizeOpt)
          .addOption(flushWaitAlertSecOpt);

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

      String output = cmd.getOptionValue(outputOpt.getLongOpt(), "output");
      List<Path> outDirs =
          Arrays.asList(output.split(","))
              .stream()
              .map(Path::of)
              .map(Path::toAbsolutePath)
              .map(Path::normalize)
              .collect(Collectors.toList());

      String bufferSize = cmd.getOptionValue(bufferSizeOpt.getLongOpt());
      BufferedAsyncCsvWriter.bufferSize = NumberUtils.toInt(bufferSize, 1000);

      String flushWaitAlertSec = cmd.getOptionValue(flushWaitAlertSecOpt.getLongOpt());
      BufferedAsyncCsvWriter.flushWaitAlertSec = NumberUtils.toInt(flushWaitAlertSec, 10);

      if (cmd.getArgList().contains("read-sql")) {
        Path out = schemaAnalyzer.analyze(input);
        log.info("json: {}", out);
      }

      if (cmd.getArgList().contains("gen-data")) {
        List<Path> outputs = dataGenerator.generate(input, outDirs);
        outputs.forEach(out -> log.info("csv: {}", out));
      }

      return 0;

    } catch (Exception e) {
      e.printStackTrace();
      return 1;
    }
  }
}
