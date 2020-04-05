package io.sitoolkit.rdg.core.infrastructure;

import java.util.Scanner;

public class ResourceUtils {

  public static String res2str(String resourceName) {
    StringBuilder sb = new StringBuilder();

    try (Scanner scanner = new Scanner(ClassLoader.getSystemResourceAsStream(resourceName))) {
      while (scanner.hasNextLine()) {
        sb.append(scanner.nextLine());
        sb.append(System.lineSeparator());
      }
    }

    return sb.toString();
  }
}
