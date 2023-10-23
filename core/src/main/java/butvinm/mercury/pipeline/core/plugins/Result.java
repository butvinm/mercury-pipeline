package butvinm.mercury.pipeline.core.plugins;

import lombok.Data;
import lombok.experimental.SuperBuilder;

/**
 * Result of plugin execution.
 */
@Data
@SuperBuilder
public class Result {
    /**
     * Determine whether event was processed or ignored.
     */
    private final Boolean processed;
}
