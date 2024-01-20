package butvinm.mercury.pipeline.handler;

import lombok.Data;

@Data
public class Transition {
    private final Filter filter;

    private final String status;

    public static Transition fromConfig(TransitionConfig config) {
        return new Transition(Filter.fromConfig(config.getFilter()),
            config.getStatus());
    }
}
