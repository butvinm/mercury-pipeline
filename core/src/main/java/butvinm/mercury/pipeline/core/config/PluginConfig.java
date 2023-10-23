package butvinm.mercury.pipeline.core.config;

import com.fasterxml.jackson.annotation.JsonTypeInfo;

import lombok.Data;
import lombok.NonNull;
import lombok.experimental.SuperBuilder;

/**
 * Plugin configuration structure.
 */
@JsonTypeInfo(
    use = JsonTypeInfo.Id.NAME,
    include = JsonTypeInfo.As.EXISTING_PROPERTY,
    property = "name",
    visible = true
)
@SuperBuilder
@Data
public abstract class PluginConfig {
    @NonNull
    private final String name;
}
