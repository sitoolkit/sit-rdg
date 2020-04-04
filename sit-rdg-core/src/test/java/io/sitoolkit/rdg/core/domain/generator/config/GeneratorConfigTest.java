package io.sitoolkit.rdg.core.domain.generator.config;

import io.sitoolkit.rdg.core.infrastructure.JsonUtils;
import java.nio.file.Path;
import org.junit.Test;

public class GeneratorConfigTest {

  private Path configJson =
      Path.of("src")
          .resolve("test")
          .resolve("resources")
          .resolve("generator-config.json")
          .toAbsolutePath()
          .normalize();

  @Test
  public void readJson() {
    GeneratorConfig setting = JsonUtils.json2object(configJson, GeneratorConfig.class);

    Scale scale = setting.getScale();
    // System.out.println(scale.getScaleStr());
    System.out.println("-----");

    setting.getSchemaConfigs().stream()
        .peek(s -> System.out.println(s.getName()))
        .flatMap(s -> s.getTableConfigs().stream())
        .forEach(
            t -> {
              System.out.println(t.getPriorityRank());
              System.out.println(t.getRowCount());
              System.out.println(t.getRequiredValueCount());
              System.out.println(t.getName());
              System.out.println("-----");
            });
  }
}
