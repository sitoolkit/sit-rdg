package io.sitoolkit.rdg.core.infrastructure;

import java.io.FileWriter;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.time.StopWatch;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class BufferedAsyncCsvWriter implements DataWriter, Runnable {

  private CSVPrinter printer;

  private Path outFilePath;

  private List<List<Object>> lines = new CopyOnWriteArrayList<>();

  public static int bufferSize = 1000;

  public static int flushWaitAlertSec = 10;

  @Getter private int totalRows = 0;

  private ExecutorService executorService = Executors.newSingleThreadExecutor();

  volatile boolean flushing = false;

  public static BufferedAsyncCsvWriter build(Path outFilePath) {
    BufferedAsyncCsvWriter writer = new BufferedAsyncCsvWriter();

    writer.outFilePath = outFilePath;

    try {
      writer.printer = new CSVPrinter(new FileWriter(outFilePath.toString()),
          CSVFormat.DEFAULT.withRecordSeparator(System.lineSeparator()));
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }

    return writer;
  }

  @Override
  public void writeAppend(List<Object> line) throws IOException {
    StopWatch stopWatch = StopWatch.createStarted();

    waitForFlushing();

    stopWatch.stop();
    if (stopWatch.getTime(TimeUnit.SECONDS) > flushWaitAlertSec) {
      log.warn("Flushing wait span is too long {}", stopWatch);
    }

    lines.add(line);

    if (lines.size() >= bufferSize && !flushing) {
      flushing = true;

      executorService.execute(this);
    }
  }

  @Override
  public void run() {
    flush();
  }

  public void flush() {
    flushing = true;

    try {

      if (lines.isEmpty()) {
        return;
      }

      for (List<Object> line : lines) {
        printer.printRecord(line);
      }

      printer.flush();
      totalRows += lines.size();
      lines.clear();

      log.debug("Flush buffer to file:{}, totalRows:{}", outFilePath.toAbsolutePath(), totalRows);

    } catch (IOException e) {
      throw new UncheckedIOException(e);
    } finally {
      flushing = false;
    }
  }

  @Override
  public void close() throws IOException {
    waitForFlushing();
    if (!lines.isEmpty()) {
      flush();
    }
    executorService.shutdown();
    printer.close();
    log.info("Close");
  }

  @Override
  public List<Path> getFiles() {
    return List.of(outFilePath);
  }

  private void waitForFlushing() {
    while(flushing) {
      // NOP
    }
  }
}
