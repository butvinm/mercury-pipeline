package butvinm.mercury.pipeline.core.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

/**
 * Plugin configuration structure.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.PROPERTY,
    property = "name"
)
public abstract class PluginConfig {}
