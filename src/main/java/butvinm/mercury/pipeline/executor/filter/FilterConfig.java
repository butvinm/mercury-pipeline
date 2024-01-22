package butvinm.mercury.pipeline.executor.filter;

import com.fasterxml.jackson.annotation.JsonProperty;

import butvinm.mercury.pipeline.models.MRState;
import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class FilterConfig {
    @JsonProperty("new_label")
    private final String newLabel;

    @JsonProperty("del_label")
    private final String deletedLabel;

    @JsonProperty("new_reviewer")
    @Builder.Default
    private final Boolean newReviewer = false;

    @JsonProperty("mr_state")
    private final MRState mrState;
}
