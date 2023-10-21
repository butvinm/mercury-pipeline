package butvinm.mercury.pipeline.providers.gitlab;

import butvinm.mercury.pipeline.core.events.Event;
import butvinm.mercury.pipeline.core.events.EventHandler;
import butvinm.mercury.pipeline.core.events.common.MREvent;
import butvinm.mercury.pipeline.providers.gitlab.utils.GLEnvMapper;
import io.vavr.collection.Map;
import io.vavr.control.Either;

/**
 * Event handler implementation for GitLab environment.
 *
 * We suppress deprecation warnings, because we use alpha version of Vavr.
 */
@SuppressWarnings("deprecation")
public class GLEventHandler implements EventHandler {
    /**
     * GitLab environ variables converter.
     */
    private final GLEnvMapper envsMapper = new GLEnvMapper(
        "CI_MERGE_REQUEST"
    );

    /**
     * Handler event from GitLab CI.
     *
     * Check that we are in the GitLab CI and handled event is Merge Request
     * (probably we would support more events in future).
     *
     * Than try map CI environments to MREvent structure via {@GLEnvMapper}.
     */
    @Override
    public Either<String, Event> handleEvent(Map<String, String> env) {
        // That weird cast is trickery to satisfy Java type system.
        return Either.<String, Map<String, String>>right(env)
            .filterOrElse(
                e -> e.get("GITLAB_CI").isDefined(),
                e -> "You are not in GitLab CI environment."
            ).filterOrElse(
                e -> e.get("CI_PIPELINE_SOURCE").contains("merge_request"),
                e -> "Can process only Merge Requests."
            ).flatMap(
                e -> envsMapper.convertEnv(e, MREvent.class)
                    .fold(
                        f -> Either.left(f.toString()),
                        s -> Either.right(s)
                    )
            );
    }
}
