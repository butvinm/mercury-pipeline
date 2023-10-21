package butvinm.mercury.pipeline.core.events;

import io.vavr.collection.Map;
import io.vavr.control.Either;

/**
 * Handle and parse {@link Event} from environment.
 *
 * Environment, actually, can be anything, but generally we meant environment
 * variables of runner from which pipeline was called.
 */
public interface EventHandler {
    Either<String, Event> handleEvent(Map<String, String> env);
}
