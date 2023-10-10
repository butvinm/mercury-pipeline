package butvinm.mercury.pipeline.core.events;

import io.vavr.collection.Map;
import io.vavr.control.Option;

/**
 * Handle and parse {@link Event} from environment.
 */
public interface EventHandler {
    Option<Event> handlerEvent(Map<String, String> env);
}
