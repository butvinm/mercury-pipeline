package butvinm.mercury.pipeline.core.plugins;

import com.fasterxml.jackson.dataformat.yaml.snakeyaml.events.Event;

/**
 * Interface of pipeline plugin.
 *
 * Plugins actually provide all pipeline functionality.
 *
 * Plugin consume {@link Event} and produce {@link Result}
 */
@FunctionalInterface
public interface Plugin {
    Result processEvent(Event event);
}
