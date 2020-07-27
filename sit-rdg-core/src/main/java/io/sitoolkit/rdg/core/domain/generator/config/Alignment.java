package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Alignment {

  @JsonProperty private int length;

  @JsonProperty private char padChar;
}
