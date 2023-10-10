package butvinm.mercury.pipeline.core.config.utils;

import butvinm.mercury.pipeline.core.config.PluginConfig;
import io.vavr.collection.List;
import io.vavr.collection.Map;
import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

@Jacksonized
@Builder
public class TestPluginConfig1 extends PluginConfig {
    private final String property1;
    private final Integer property2;
    private final List<String> stringList;
    private final Map<String, Integer> stringToIntMap;
}