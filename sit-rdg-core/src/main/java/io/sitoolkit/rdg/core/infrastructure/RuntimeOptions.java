package io.sitoolkit.rdg.core.infrastructure;

import lombok.Data;

@Data
public class RuntimeOptions {

  private static final RuntimeOptions INSTANCE = new RuntimeOptions();

  private int bufferSize = 1000;

  private int flushWaitAlertSec = 10;

  public static RuntimeOptions getInstance() {
    return INSTANCE;
  }
}
