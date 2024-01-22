package butvinm.mercury.pipeline.executor.definition;

import java.util.regex.Pattern;

import butvinm.mercury.pipeline.YTClient;
import butvinm.mercury.pipeline.executor.Executor;
import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.filter.Filter;
import butvinm.mercury.pipeline.executor.transition.Transition;
import butvinm.mercury.pipeline.models.MRState;
import lombok.Data;

@Data
public class ExecutorDefinition {
    private final YTClient yt;

    private Executor.ExecutorBuilder executor = Executor.builder();

    private Transition.TransitionBuilder transition = Transition.builder();

    private Filter.FilterBuilder filter = Filter.builder();

    private final ExecutorFSM executorFsm = new ExecutorFSM();

    private final TransitionFSM transitionFsm = new TransitionFSM();

    public ExecutorDefinition mrNamePattern(String pattern)
        throws DefinitionException {
        executor.mrNamePattern(Pattern.compile(pattern));
        executorFsm.visitMRNamePattern();
        ;
        return this;
    }

    public ExecutorDefinition transitions() throws DefinitionException {
        executorFsm.visitTransitions();
        return this;
    }

    public ExecutorDefinition when() throws DefinitionException {
        if (transitionFsm.isWhenVisited()) {
            executor.transition(transition.build());
            transition = Transition.builder();
            transitionFsm.reset();
        }
        transitionFsm.visitWhen();
        return this;
    }

    public ExecutorDefinition mrState(MRState state)
        throws DefinitionException {
        filter.mrState(state);
        return this;
    }

    public ExecutorDefinition newLabel(String title)
        throws DefinitionException {
        filter.newLabel(title);
        return this;
    }

    public ExecutorDefinition delLabel(String title)
        throws DefinitionException {
        filter.delLabel(title);
        return this;
    }

    public ExecutorDefinition newReviewer() throws DefinitionException {
        filter.newReviewer();
        return this;
    }

    public ExecutorDefinition status(String status)
        throws DefinitionException {
        if (transitionFsm.isStatusVisited()) {
            executor.transition(transition.build());
            transition = Transition.builder();
            transitionFsm.reset();
        }
        if (transitionFsm.isWhenVisited()) {
            transition.filter(filter.build());
            filter = Filter.builder();
        }
        transition.status(status);
        transitionFsm.visitStatus();
        return this;
    }

    public Executor define() throws DefinitionException {
        if (executorFsm.isTransitionsVisited()) {
            executor.transition(transition.build());
            transition = Transition.builder();
            transitionFsm.reset();
        }
        executor.yt(yt);
        return executor.build();
    }
}
