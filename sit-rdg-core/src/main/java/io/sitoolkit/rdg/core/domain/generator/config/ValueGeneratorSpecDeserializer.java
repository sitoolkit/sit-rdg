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

    // switch (type) {
    //   case "sequence":
    //     generatorType = SequenceValueGenerator.class;
    //     break;
    //   case "branchNumber":
    //     generatorType = MultiSequenceValueGenerator.class;
    //     break;
    //   case "choice":
    //     generatorType = ChoiceValueGenerator.class;
    //     break;
    //   case "const":
    //     generatorType = ConstantGenerator.class;
    //     break;
    //   case "date":
    //     generatorType = DateValueGenerator.class;
    //     break;
    //   case "range":
    //     generatorType = RangeValueGenerator.class;
    //     break;
    //   default:
    //     generatorType = RandomGenerator.class;
    // }

    ValueGenerator generator = mapper.readValue(node.toString(), generatorType);

    generator.initialize();

    return generator;
  }
}
