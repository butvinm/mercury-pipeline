package butvinm.mercury.pipeline.yt.models;

import com.fasterxml.jackson.annotation.JsonValue;

import lombok.Getter;

public enum Resolution {
    FIXED("fixed"),
    WONTFIX("wontFix"),
    CANTREPRODUCE("cantReproduce"),
    DUPLICATE("duplicate"),
    LATER("later");

    @Getter
    @JsonValue
    private final String label;

    private Resolution(String label) {
        this.label = label;
    }
}
