package io.sitoolkit.rdg.core.domain.generator.config;

public class IllegalConfigException extends RuntimeException {

  public IllegalConfigException() {}

  public IllegalConfigException(String message) {
    super(message);
  }

  public IllegalConfigException(Throwable cause) {
    super(cause);
  }

  public IllegalConfigException(String message, Throwable cause) {
    super(message, cause);
  }

  public IllegalConfigException(
      String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
