package butvinm.mercury.pipeline.handler;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class TransitionConfig {
    @JsonProperty("when")
    private final FilterConfig filter;

    @JsonProperty("status")
    private final String status;
}
