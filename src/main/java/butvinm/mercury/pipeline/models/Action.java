package butvinm.mercury.pipeline.models;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

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
