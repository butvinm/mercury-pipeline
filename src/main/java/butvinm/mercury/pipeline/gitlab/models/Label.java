package butvinm.mercury.pipeline.gitlab.models;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class Label {
    @JsonProperty("id")
    private final int id;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("color")
    private final String color;

    @JsonProperty("project_id")
    private final int projectId;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    @JsonProperty("template")
    private final boolean template;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("type")
    private final String type;

    @JsonProperty("group_id")
    private final int groupId;
}
