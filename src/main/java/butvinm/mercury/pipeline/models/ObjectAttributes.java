package butvinm.mercury.pipeline.models;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class ObjectAttributes {
    @JsonProperty("id")
    private final int id;

    @JsonProperty("iid")
    private final int iid;

    @JsonProperty("target_branch")
    private final String targetBranch;

    @JsonProperty("source_branch")
    private final String sourceBranch;

    @JsonProperty("source_project_id")
    private final int sourceProjectId;

    @JsonProperty("author_id")
    private final int authorId;

    @JsonProperty("assignee_ids")
    private final List<Integer> assigneeIds;

    @JsonProperty("assignee_id")
    private final int assigneeId;

    @JsonProperty("reviewer_ids")
    private final List<Integer> reviewerIds;

    @JsonProperty("title")
    private final String title;

    @JsonProperty("created_at")
    private final String createdAt;

    @JsonProperty("updated_at")
    private final String updatedAt;

    @JsonProperty("last_edited_at")
    private final String lastEditedAt;

    @JsonProperty("last_edited_by_id")
    private final int lastEditedById;

    @JsonProperty("milestone_id")
    private final Integer milestoneId; // Change the type if needed

    @JsonProperty("state_id")
    private final int stateId;

    @JsonProperty("state")
    private final String state;

    @JsonProperty("blocking_discussions_resolved")
    private final boolean blockingDiscussionsResolved;

    @JsonProperty("work_in_progress")
    private final boolean workInProgress;

    @JsonProperty("draft")
    private final boolean draft;

    @JsonProperty("first_contribution")
    private final boolean firstContribution;

    @JsonProperty("merge_status")
    private final String mergeStatus;

    @JsonProperty("target_project_id")
    private final int targetProjectId;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("total_time_spent")
    private final int totalTimeSpent;

    @JsonProperty("time_change")
    private final int timeChange;

    @JsonProperty("human_total_time_spent")
    private final String humanTotalTimeSpent;

    @JsonProperty("human_time_change")
    private final String humanTimeChange;

    @JsonProperty("human_time_estimate")
    private final String humanTimeEstimate;

    @JsonProperty("url")
    private final String url;

    @JsonProperty("source")
    private final Project source;

    @JsonProperty("target")
    private final Project target;

    @JsonProperty("last_commit")
    private final Commit lastCommit;

    @JsonProperty("labels")
    private final List<Label> labels;

    @JsonProperty("action")
    private final Action action;

    @JsonProperty("detailed_merge_status")
    private final String detailedMergeStatus;

    public enum Action {
        OPEN("open"),
        CLOSE("close"),
        REOPEN("reopen"),
        UPDATE("update"),
        APPROVED("approved"),
        UNAPPROVED("unapproved"),
        APPROVAL("approval"),
        UNAPPROVAL("unapproval"),
        MERGE("merge");

        @Getter
        @JsonValue
        private final String label;

        private Action(String label) {
            this.label = label;
        }
    }
}
