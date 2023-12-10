package butvinm.mercury.pipeline.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class Repository {
    @JsonProperty("name")
    private final String name;

    @JsonProperty("url")
    private final String url;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("homepage")
    private final String homepage;
}
