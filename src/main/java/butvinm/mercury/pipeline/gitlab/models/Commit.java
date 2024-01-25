package butvinm.mercury.pipeline.gitlab.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class Commit {
    @JsonProperty("id")
    private final String id;

    @JsonProperty("message")
    private final String message;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("timestamp")
    private final String timestamp;

    @JsonProperty("author")
    private final User author;
}
