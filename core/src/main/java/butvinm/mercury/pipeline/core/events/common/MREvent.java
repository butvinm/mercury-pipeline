package butvinm.mercury.pipeline.core.events.common;

import butvinm.mercury.pipeline.core.events.Event;
import io.vavr.collection.List;
import lombok.Builder;
import lombok.Data;
import lombok.NonNull;
import lombok.extern.jackson.Jacksonized;

/**
 * MergeRequest event.
 *
 * Contains information about merge request.
 *
 * @see <a
 *      href=https://docs.gitlab.com/ee/ci/variables/predefined_variables.html>GitLab
 *      Docs</a>
 */
@Data
@Builder
@Jacksonized
public class MREvent implements Event {
    /**
     * The instance-level ID of the merge request. This is a unique ID across
     * all projects on GitLab.
     */
    @NonNull
    private final Long id;

    /**
     * The project-level IID (internal ID) of the merge request. This ID is
     * unique for the current project.
     */
    @NonNull
    private final Long iid;

    /**
     * The title of the merge request.
     */
    @NonNull
    private final String title;

    /**
     * The event type of the merge request. Can be detached, merged_result, or
     * merge_train.
     */
    @NonNull
    private final MREventType eventType;

    /**
     * Approval status of the merge request. True when merge request approvals
     * are available and the merge request has been approved.
     */
    @NonNull
    private final Boolean approved;

    /**
     * Comma-separated list of usernames of assignees for the merge request.
     */
    @NonNull
    private final List<String> assignees;

    /**
     * Comma-separated label names of the merge request.
     */
    @NonNull
    private final List<String> labels;

    /**
     * The milestone title of the merge request.
     */
    @NonNull
    private final String milestone;

    /**
     * The ID of the project of the merge request.
     */
    @NonNull
    private final Long projectId;

    /**
     * The path of the project of the merge request. For example,
     * namespace/awesome-project.
     */
    @NonNull
    private final String projectPath;

    /**
     * The URL of the project of the merge request. For example,
     * http://192.168.10.15:3000/namespace/awesome-project.
     */
    @NonNull
    private final String projectUrl;

    /**
     * The ref path of the merge request. For example,
     * refs/merge-requests/1/head.
     */
    @NonNull
    private final String refPath;

    /**
     * True when the squash on merge option is set.
     */
    @NonNull
    private final Boolean squashOnMerge;

    /**
     * The source branch name of the merge request.
     */
    @NonNull
    private final String sourceBranchName;

    /**
     * True when the source branch of the merge request is protected.
     */
    @NonNull
    private final Boolean sourceBranchProtected;

    /**
     * The HEAD SHA of the source branch of the merge request. The variable is
     * empty in merge request pipelines. The SHA is present only in merged
     * results pipelines.
     */
    @NonNull
    private final String sourceBranchSha;

    /**
     * The ID of the source project of the merge request.
     */
    @NonNull
    private final Long sourceProjectId;

    /**
     * The path of the source project of the merge request.
     */
    @NonNull
    private final String sourceProjectPath;

    /**
     * The URL of the source project of the merge request.
     */
    @NonNull
    private final String sourceProjectUrl;

    /**
     * The target branch name of the merge request.
     */
    @NonNull
    private final String targetBranchName;

    /**
     * True when the target branch of the merge request is protected.
     */
    @NonNull
    private final Boolean targetBranchProtected;

    /**
     * The HEAD SHA of the target branch of the merge request. The variable is
     * empty in merge request pipelines. The SHA is present only in merged
     * results pipelines.
     */
    @NonNull
    private final String targetBranchSha;

    /**
     * The version of the merge request diff.
     */
    @NonNull
    private final Long diffId;

    /**
     * The base SHA of the merge request diff.
     */
    @NonNull
    private final String diffBaseSha;
}
