package butvinm.mercury.pipeline.executor.trigger;

import java.util.Optional;
import java.util.function.Function;

import butvinm.mercury.pipeline.executor.filter.Filter;
import butvinm.mercury.pipeline.models.MREvent;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Trigger {
    private final Filter filter;

    private final Function<MREvent, String> action;

    public Optional<String> run(MREvent event) {
        if (!filter.test(event)) {
            return Optional.empty();
        }
        return Optional.of(action.apply(event));
    }
}
