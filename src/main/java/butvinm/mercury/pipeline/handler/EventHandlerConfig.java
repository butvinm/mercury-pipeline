package butvinm.mercury.pipeline.handler;

import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class EventHandlerConfig {
    @JsonProperty("mr_name_pattern")
    private final Pattern mrNamePattern;

    @JsonProperty("transitions")
    private final List<TransitionConfig> transitions;
}
