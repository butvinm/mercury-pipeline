package butvinm.mercury.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import java.util.function.Function;
import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import butvinm.mercury.pipeline.executor.Executor;
import butvinm.mercury.pipeline.executor.definition.ExecutorDefinition;
import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.definition.exceptions.InvalidSpecException;
import butvinm.mercury.pipeline.executor.filter.Filter;
import butvinm.mercury.pipeline.executor.transition.Transition;
import butvinm.mercury.pipeline.executor.trigger.Trigger;
import butvinm.mercury.pipeline.gitlab.models.MREvent;
import butvinm.mercury.pipeline.gitlab.models.MRState;
import butvinm.mercury.pipeline.yt.YTClient;
import butvinm.mercury.pipeline.yt.models.Resolution;

public class ExecutorBuilderTest {
    private static final YTClient yt = new YTClient("test", "test");

    private final String MR_NAME_PATTERN = "\\w+-(?<issueId>[\\w-]+)";

    private final Function<MREvent, String> dummyAction = new Function<>() {
        @Override
        public String apply(MREvent event) {
            return event.toString();
        }
    };

    private void assertExecutorsEquals(Executor expected, Executor actual) {
        assertEquals(
            expected.getMrNamePattern().pattern(),
            actual.getMrNamePattern().pattern()
        );
        assertEquals(
            expected.getTransitions(),
            actual.getTransitions()
        );
        assertEquals(
            expected.getTriggers(),
            actual.getTriggers()
        );
    }

    @Test
    public void testFull() throws DefinitionException {
        var actual = new ExecutorDefinition(yt)
        .mrNamePattern(MR_NAME_PATTERN)
        .transitions()
            .when()
                .newReviewer()
            .status("in_review")

            .when()
                .newLabel("rejected")
                .mrState(MRState.CLOSE)
            .status("need_info")

            .when()
                .delLabel("rejected")
            .status("close")
            .resolution(Resolution.LATER)
        .triggers()
            .when()
                .newReviewer()
            .action(dummyAction)

            .when()
                .newLabel("rejected")
                .mrState(MRState.CLOSE)
            .action(dummyAction)

            .when()
                .delLabel("rejected")
            .action(dummyAction)
        .define();

        var expected = Executor.builder()
            .yt(yt)
            .mrNamePattern(Pattern.compile(MR_NAME_PATTERN))
            .transition(
                Transition.builder()
                    .filter(
                        Filter.builder()
                            .newReviewer()
                            .build()
                    )
                    .status("in_review")
                    .build()
            )
            .transition(
                Transition.builder()
                    .filter(
                        Filter.builder()
                            .newLabel("rejected")
                            .mrState(MRState.CLOSE)
                            .build()
                    )
                    .status("need_info")
                    .build()
            )
            .transition(
                Transition.builder()
                    .filter(
                        Filter.builder()
                            .delLabel("rejected")
                            .build()
                    )
                    .status("close")
                    .resolution(Resolution.LATER)
                    .build()
            )
            .trigger(
                Trigger.builder()
                    .filter(
                        Filter.builder()
                            .newReviewer()
                            .build()
                    )
                    .action(dummyAction)
                    .build()
            )
            .trigger(
                Trigger.builder()
                    .filter(
                        Filter.builder()
                            .newLabel("rejected")
                            .mrState(MRState.CLOSE)
                            .build()
                    )
                    .action(dummyAction)
                    .build()
            )
            .trigger(
                Trigger.builder()
                    .filter(
                        Filter.builder()
                            .delLabel("rejected")
                            .build()
                    )
                    .action(dummyAction)
                    .build()
            )
            .build();

        assertExecutorsEquals(expected, actual);
    }

    @Test
    public void testMissedFields() throws DefinitionException {
        assertThrows(
            InvalidSpecException.class,
            () -> new ExecutorDefinition(yt).define()
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .transitions()
                    .when()
                        .newReviewer()
                    .status("in_review")
                .define();
            }
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .triggers()
                    .when()
                        .newReviewer()
                    .action(dummyAction)
                .define();
            }
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .mrNamePattern(MR_NAME_PATTERN)
                .define();
            }
        );
    }

    @Test
    public void testTwiceDeclaration() {
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .mrNamePattern(MR_NAME_PATTERN)
                .transitions()
                .mrNamePattern(MR_NAME_PATTERN)
                .define();
            }
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .mrNamePattern(MR_NAME_PATTERN)
                .transitions()
                    .when()
                        .newReviewer()
                    .resolution(Resolution.FIXED)
                    .resolution(Resolution.LATER)
                .define();
            }
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .transitions()
                .mrNamePattern(MR_NAME_PATTERN)
                .transitions()
                .define();
            }
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .triggers()
                .mrNamePattern(MR_NAME_PATTERN)
                .triggers()
                .define();
            }
        );
    }
}
