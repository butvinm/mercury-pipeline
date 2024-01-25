package butvinm.mercury.pipeline.executor.transition;

import java.util.Optional;

import butvinm.mercury.pipeline.executor.filter.Filter;
import butvinm.mercury.pipeline.gitlab.models.MREvent;
import butvinm.mercury.pipeline.yt.YTClient;
import butvinm.mercury.pipeline.yt.models.Resolution;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class Transition {
    private final Filter filter;

    private final String status;

    @Builder.Default
    private final Resolution resolution = Resolution.FIXED;

    public static Transition fromConfig(TransitionConfig config) {
        return new Transition(
            Filter.fromConfig(config.getFilter()),
            config.getStatus(),
            config.getResolution()
        );
    }

    public Optional<String> transit(
        YTClient yt,
        String issueId,
        MREvent event
    ) {
        if (!filter.test(event)) {
            return Optional.empty();
        }
        var response = yt.transitIssueStatus(issueId, status, resolution);
        var result = "Transit issue %s to %s: %s\n".formatted(
            issueId,
            status,
            response.getBody()
        );
        return Optional.of(result);
    }
}
