package butvinm.mercury.pipeline.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class Changes {
    @JsonProperty("reviewers")
    private final Reviewers reviewers;

    @JsonProperty("labels")
    private final Labels labels;

    @Data
    @Jacksonized
    @Builder
    public static class Reviewers {
        @JsonProperty("previous")
        private final List<User> previous;

        @JsonProperty("current")
        private final List<User> current;
    }

    @Data
    @Jacksonized
    @Builder
    public static class Labels {
        @JsonProperty("previous")
        private final List<Label> previous;

        @JsonProperty("current")
        private final List<Label> current;
    }
}
