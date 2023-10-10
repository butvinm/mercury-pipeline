package butvinm.mercury.pipeline.core.config;

import io.vavr.collection.List;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * Pipeline configuration structure.
 */
@Jacksonized
@Builder
@Data
public class PipelineConfig {
    /**
     * Plugins configurations.
     */
    @Builder.Default
    private final List<PluginConfig> plugins = List.of();
}
