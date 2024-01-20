package butvinm.mercury.pipeline.handler;

import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import butvinm.mercury.pipeline.YTClient;
import butvinm.mercury.pipeline.models.MREvent;
import lombok.Data;

@Data
public class EventHandler {
    private final YTClient yt;

    private final Pattern mrNamePattern;

    private final List<Transition> transitions;

    public static EventHandler fromConfig(YTClient yt,
        EventHandlerConfig config) {
        var transitions = config.getTransitions().stream()
            .map(Transition::fromConfig)
            .toList();
        return new EventHandler(yt, config.getMrNamePattern(), transitions);
    }

    public Optional<String> processEvent(MREvent event) {
        var issueId = this.getIssueId(event);
        if (issueId.isEmpty()) {
            return Optional.empty();
        }
        var resultBuilder = new StringBuilder();
        for (var transition : this.transitions) {
            if (transition.getFilter().test(event)) {
                var response = yt.transitIssueStatus(
                    issueId.get(),
                    transition.getStatus()
                );
                resultBuilder.append("Transit issue %s to %s: \n".formatted(
                    issueId.get(),
                    transition.getStatus(),
                    response.getBody()
                ));
            }
        }
        return Optional.of(resultBuilder.toString());
    }

    private Optional<String> getIssueId(MREvent event) {
        var matcher = mrNamePattern
            .matcher(event.getObjectAttributes().getTitle());
        if (matcher.find()) {
            return Optional.of(matcher.group("issueId"));
        }
        return Optional.empty();
    }
}
