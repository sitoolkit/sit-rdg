package io.sitoolkit.rdg.core.domain.generator.config;

import java.util.ArrayList;
import java.util.List;
import lombok.Data;

@Data
public class RelationConfig {

  private List<String> subColumns = new ArrayList<>();

  private List<MultiplicityConfig> multiplicities = new ArrayList<>();
}
