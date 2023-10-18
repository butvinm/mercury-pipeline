package butvinm.mercury.pipeline.core.events;

import io.vavr.collection.Map;
import io.vavr.control.Option;

/**
 * Handle and parse {@link Event} from environment.
 */
public interface EventHandler {
    Option<Event> handleEvent(Map<String, String> env);
}
