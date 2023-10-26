package butvinm.mercury.pipeline.core.plugins;

import butvinm.mercury.pipeline.core.events.Event;

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
