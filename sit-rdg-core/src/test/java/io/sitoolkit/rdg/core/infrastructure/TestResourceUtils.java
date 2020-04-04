package io.sitoolkit.rdg.core.infrastructure;

import java.net.URISyntaxException;
import java.nio.file.Path;

public class TestResourceUtils {

  public static Path res2path(Object owner, String resourceName) {
    try {
      return Path.of(owner.getClass().getResource(resourceName).toURI());
    } catch (URISyntaxException e) {
      throw new IllegalArgumentException(e);
    }
  }
}
