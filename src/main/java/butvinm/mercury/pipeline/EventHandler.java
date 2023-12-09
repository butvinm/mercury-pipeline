package butvinm.mercury.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;

import butvinm.mercury.pipeline.models.MREvent;
import lombok.RequiredArgsConstructor;

public class EventHandler {
    private final List<Function<MREvent, Optional<String>>> handlers = new ArrayList<>();

    public List<String> handle(MREvent event) {
        return handlers.stream()
            .map(handler -> handler.apply(event))
            .filter(r -> r.isPresent())
            .map(r -> r.get())
            .toList();
    }

    public HandlerCapture when(EventFilter filter) {
        return new HandlerCapture(filter);
    }

    @RequiredArgsConstructor
    public class HandlerCapture {
        private final EventFilter filter;

        public void run(Function<MREvent, String> callback) {
            handlers.add(event -> {
                if (filter.test(event)) {
                    return Optional.of(callback.apply(event));
                }
                return Optional.empty();
            });
        }
    }
}
