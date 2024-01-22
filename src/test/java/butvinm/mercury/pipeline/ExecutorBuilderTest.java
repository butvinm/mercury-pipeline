package butvinm.mercury.pipeline;

import static org.junit.jupiter.api.Assertions.*;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import butvinm.mercury.pipeline.executor.Executor;
import butvinm.mercury.pipeline.executor.definition.ExecutorDefinition;
import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.definition.exceptions.InvalidSpecException;
import butvinm.mercury.pipeline.executor.filter.Filter;
import butvinm.mercury.pipeline.executor.transition.Transition;
import butvinm.mercury.pipeline.models.MRState;

public class ExecutorBuilderTest {
    private static final YTClient yt = new YTClient("test", "test");

    private void assertExecutorsEquals(Executor expected, Executor actual) {
        assertEquals(
            expected.getMrNamePattern().pattern(),
            actual.getMrNamePattern().pattern()
        );
        assertEquals(
            expected.getTransitions(),
            actual.getTransitions()
        );
    }

    @Test
    public void testFull() throws DefinitionException {
        var actual = new ExecutorDefinition(yt)
        .mrNamePattern("\\w+-(?<issueId>[\\w-]+)")
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
            .status("in_work")
        .define();

        var expected = Executor.builder()
            .yt(yt)
            .mrNamePattern(Pattern.compile("\\w+-(?<issueId>[\\w-]+)"))
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
                    .status("in_work")
                    .build()
            )
            .build();

        assertExecutorsEquals(expected, actual);
    }

    @Test
    public void testEmpty() {
        assertThrows(NullPointerException.class, new ExecutorDefinition(yt)::define);
    }

    @Test
    public void testSkipMRNamePattern() throws DefinitionException {
        var builder = new ExecutorDefinition(yt)
        .transitions()
            .when()
                .newReviewer()
            .status("in_review")

            .when()
                .newLabel("approved")
            .status("approved");

        assertThrows(NullPointerException.class, builder::define);
    }

    @Test
    public void testSkipTransitions() throws DefinitionException {
        var actual = new ExecutorDefinition(yt)
        .mrNamePattern("\\w+-(?<issueId>[\\w-]+)")
        .define();

        var expected = Executor.builder()
            .yt(yt)
            .mrNamePattern(Pattern.compile("\\w+-(?<issueId>[\\w-]+)"))
            .build();

        assertExecutorsEquals(expected, actual);
    }

    @Test
    public void testTwiceDeclaration() {
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .mrNamePattern("\\w+-(?<issueId>[\\w-]+)")
                .transitions()
                .mrNamePattern("\\w+-(?<issueId>[\\w-]+)")
                .define();
            }
        );
        assertThrows(
            InvalidSpecException.class,
            () -> {
                new ExecutorDefinition(yt)
                .transitions()
                    .when()
                .mrNamePattern("\\w+-(?<issueId>[\\w-]+)")
                .transitions()
                .define();
            }
        );
    }
}
