package butvinm.mercury.pipeline.executor;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import butvinm.mercury.pipeline.executor.definition.ExecutorDefinition;
import butvinm.mercury.pipeline.executor.transition.Transition;
import butvinm.mercury.pipeline.executor.trigger.Trigger;
import butvinm.mercury.pipeline.gitlab.models.MREvent;
import butvinm.mercury.pipeline.yt.YTClient;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NonNull;
import lombok.Singular;

@Data
@Builder
@EqualsAndHashCode
public class Executor {
    @NonNull
    private final YTClient yt;

    @NonNull
    private final Pattern mrNamePattern;

    @NonNull
    @Singular
    private final List<Transition> transitions;

    @NonNull
    @Singular
    private final List<Trigger> triggers;

    public static ExecutorDefinition definition(YTClient yt) {
        return new ExecutorDefinition(yt);
    }

    public static Executor fromConfig(
        YTClient yt,
        ExecutorConfig config
    ) {
        var transitions = config.getTransitions().stream()
            .map(Transition::fromConfig)
            .toList();
        return new Executor(
            yt,
            config.getMrNamePattern(),
            transitions,
            new ArrayList<>()
        );
    }

    public String processEvent(MREvent event) {
        var digest = new StringBuilder();

        var issueId = this.getIssueId(event);
        if (issueId.isEmpty()) {
            digest.append("Cannot extract issue id. Check mrNamePattern.");
            return digest.toString();
        }

        for (var transition : this.transitions) {
            var transitionResult = transition.transit(yt, issueId.get(), event);
            if (transitionResult.isPresent()) {
                digest.append(transitionResult.get()).append("\n");
            }
        }

        for (var trigger : triggers) {
            var triggerResult = trigger.run(event);
            if (triggerResult.isPresent()) {
                digest.append(triggerResult.get()).append("\n");
            }
        }

        return digest.toString();
    }

    private Optional<String> getIssueId(MREvent event) {
        var matcher = mrNamePattern
            .matcher(event.getAttributes().getTitle());
        if (matcher.find()) {
            return Optional.of(matcher.group("issueId"));
        }
        return Optional.empty();
    }
}
