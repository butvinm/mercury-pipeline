package butvinm.mercury.pipeline.core.events.common;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Possible event types of Merge Request.
 */
public enum MREventType {
    /**
     * Single merge request.
     */
    @JsonProperty("detached")
    DETACHED,

    /**
     * Result of merged branch.
     */
    @JsonProperty("merged_result")
    MERGE_RESULT,

    /**
     * Checks for several MRs.
     */
    @JsonProperty("merge_train")
    MERGE_TRAIN
}
