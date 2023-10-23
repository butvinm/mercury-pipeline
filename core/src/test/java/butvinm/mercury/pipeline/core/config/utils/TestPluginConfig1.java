package butvinm.mercury.pipeline.core.config.utils;

import com.fasterxml.jackson.annotation.JsonTypeName;

import butvinm.mercury.pipeline.core.config.PluginConfig;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@SuperBuilder
@JsonTypeName("plugin1")
public class TestPluginConfig1 extends PluginConfig {
    private final String property1;
    private final Integer property2;
    private final List<String> stringList;
    private final Map<String, Integer> stringToIntMap;
}
