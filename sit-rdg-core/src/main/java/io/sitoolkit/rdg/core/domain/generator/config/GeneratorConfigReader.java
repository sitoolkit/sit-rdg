package io.sitoolkit.rdg.core.domain.generator.config;

import java.nio.file.Path;

import io.sitoolkit.rdg.core.infrastructure.JsonUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GeneratorConfigReader {

  public static final String CONFIG_NAME = "generator-config.json";

  public GeneratorConfig read(Path path) {
    Path configPath = path.resolve(CONFIG_NAME).toAbsolutePath().normalize();
    try {
      GeneratorConfig config = JsonUtils.json2object(configPath, GeneratorConfig.class);
      log.debug("Succeeded to parse config json:{}", configPath);
      return config;
    } catch (Exception e) {
      if (configPath.toFile().exists()) {
        throw new RuntimeException(e);
      }
      log.warn("Failed to parse config json:{}", configPath);
      return new GeneratorConfig();
    }
  }
}
