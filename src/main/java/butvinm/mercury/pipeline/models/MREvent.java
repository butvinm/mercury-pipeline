package butvinm.mercury.pipeline.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

/**
 * https://docs.gitlab.com/ee/user/project/integrations/webhook_events.html#merge-request-events
 */
@Data
@Jacksonized
@Builder
public class MREvent {
    @JsonProperty("object_kind")
    private final String objectKind;

    @JsonProperty("event_type")
    private final String eventType;

    @JsonProperty("user")
    private final User user;

    @JsonProperty("project")
    private final Project project;

    @JsonProperty("repository")
    private final Repository repository;

    @JsonProperty("object_attributes")
    private final Attributes attributes;

    @JsonProperty("labels")
    private final List<Label> labels;

    @JsonProperty("changes")
    private final Changes changes;

    @JsonProperty("assignees")
    private final List<User> assignees;

    @JsonProperty("reviewers")
    private final List<User> reviewers;
}
