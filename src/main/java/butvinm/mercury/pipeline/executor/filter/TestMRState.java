package butvinm.mercury.pipeline.executor.filter;

import java.util.function.Predicate;

import butvinm.mercury.pipeline.models.MREvent;
import butvinm.mercury.pipeline.models.MRState;
import lombok.Data;

@Data
class TestMRState implements Predicate<MREvent> {
    private final MRState state;

    @Override
    public boolean test(MREvent event) {
        var action = event.getAttributes().getAction();
        return action != null && action.equals(state);
    }
}
