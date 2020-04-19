package io.sitoolkit.rdg.core.infrastructure;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;

@Slf4j
public class TestResourceUtils {

  @Deprecated
  public static Path res2path(Object owner, String resourceName) {
    String packagePath = owner.getClass().getPackageName().replace(".", "/");
    return Path.of("src/test/resources", packagePath, resourceName);
  }

  @Deprecated
  public static Path copy(Object owner, String resourceName, Path toDir) {

    try {
      if (toDir.toFile().exists()) {
        FileUtils.deleteDirectory(toDir.toFile());
        log.info("Delete: {}", toDir);
      }

      toDir.toFile().mkdirs();
      log.info("Make directory: {}", toDir);

      Path srcFile = res2path(owner, resourceName);

      Path dstFile = toDir.resolve(srcFile.getFileName());
      Files.copy(srcFile, dstFile);
      log.info("Copy {} to {}", srcFile, dstFile);
      return dstFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path workDir(Object testClass, String testMethod) {
    return Path.of("target", testClass.getClass().getSimpleName(), testMethod);
  }

  public static Path copyResDir(Object testClass, String testMethod) {
    Path dstDir = workDir(testClass, testMethod).resolve("in");
    copyResDir(testClass, testMethod, dstDir);
    return dstDir;
  }

  public static void copyResDir(Object testClass, String testMethod, Path dstDir) {
    String packagePath = testClass.getClass().getName().replace(".", "/");
    Path srcDir = Path.of("src/test/resources", packagePath, testMethod);
    try {
      FileUtils.copyDirectory(srcDir.toFile(), dstDir.toFile());
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path copy(Object owner, String sub, String resourceName, Path toDir) {

    try {
      Path srcFile = res2path(owner, sub, resourceName);

      if (!srcFile.toFile().exists()) {
        return srcFile;
      }

      if (!toDir.toFile().exists()) {
        toDir.toFile().mkdirs();
        log.info("Make directory: {}", toDir);
      }

      Path dstFile = toDir.resolve(srcFile.getFileName());
      Files.copy(srcFile, dstFile);
      log.info("Copy {} to {}", srcFile, dstFile);
      return dstFile;
    } catch (IOException e) {
      throw new UncheckedIOException(e);
    }
  }

  public static Path res2path(Object owner, String sub, String resourceName) {
    String packagePath = owner.getClass().getName().replace(".", "/");
    return Path.of("src/test/resources", packagePath, sub, resourceName);
  }
}
