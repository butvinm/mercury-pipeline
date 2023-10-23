package butvinm.mercury.pipeline.core.config.utils;

import com.fasterxml.jackson.annotation.JsonTypeName;

import butvinm.mercury.pipeline.core.config.PluginConfig;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.collection.Set;
import lombok.experimental.SuperBuilder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@SuperBuilder
@JsonTypeName("plugin2")
public class TestPluginConfig2 extends PluginConfig {
    private final Boolean enabled;
    private final Double doubleValue;
    private final Set<Integer> integerSet;
    private final Map<String, List<String>> nestedMap;
}
