package butvinm.mercury.pipeline.executor.transition;

import com.fasterxml.jackson.annotation.JsonProperty;

import butvinm.mercury.pipeline.executor.filter.FilterConfig;
import butvinm.mercury.pipeline.yt.models.Resolution;
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

    @JsonProperty("resolution")
    @Builder.Default
    private final Resolution resolution = Resolution.FIXED;
}
