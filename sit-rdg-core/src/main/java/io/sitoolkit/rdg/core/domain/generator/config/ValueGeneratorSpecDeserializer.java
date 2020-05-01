package io.sitoolkit.rdg.core.domain.generator.config;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.sitoolkit.rdg.core.domain.value.ValueGenerator;
import java.io.IOException;
import org.apache.commons.lang3.StringUtils;

public class ValueGeneratorSpecDeserializer extends JsonDeserializer<ValueGenerator> {

  @Override
  public ValueGenerator deserialize(JsonParser p, DeserializationContext ctx) throws IOException {

    ObjectMapper mapper = (ObjectMapper) p.getCodec();
    JsonNode node = mapper.readTree(p);

    String type = node.get("type").textValue();
    String className =
        getClass().getPackageName() + "." + StringUtils.capitalize(type) + "ValueGenerator";
    Class<? extends ValueGenerator> generatorType;
    try {
      generatorType = (Class<? extends ValueGenerator>) Class.forName(className);
    } catch (ClassNotFoundException e) {
      throw new IllegalArgumentException(e);
    }

    ValueGenerator generator = mapper.readValue(node.toString(), generatorType);

    generator.initialize();

    return generator;
  }
}
