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

    @Data
    @Jacksonized
    @Builder
    public static class Reviewers {
        @JsonProperty("previous")
        private final List<User> previous;

        @JsonProperty("current")
        private final List<User> current;
    }
}
