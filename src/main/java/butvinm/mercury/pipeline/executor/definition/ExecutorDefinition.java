package butvinm.mercury.pipeline.executor.definition;

import java.util.function.Function;
import java.util.function.Predicate;
import java.util.regex.Pattern;

import butvinm.mercury.pipeline.executor.Executor;
import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.definition.exceptions.InvalidSpecException;
import butvinm.mercury.pipeline.executor.filter.Filter;
import butvinm.mercury.pipeline.executor.transition.Transition;
import butvinm.mercury.pipeline.executor.trigger.Trigger;
import butvinm.mercury.pipeline.gitlab.models.MREvent;
import butvinm.mercury.pipeline.gitlab.models.MRState;
import butvinm.mercury.pipeline.yt.YTClient;
import butvinm.mercury.pipeline.yt.models.Resolution;
import lombok.Data;

@Data
public class ExecutorDefinition {
    private final YTClient yt;

    private Executor.ExecutorBuilder executor = Executor.builder();

    private Transition.TransitionBuilder transition = Transition.builder();

    private Trigger.TriggerBuilder trigger = Trigger.builder();

    private Filter.FilterBuilder filter = Filter.builder();

    private final ExecutorFSM executorFsm = new ExecutorFSM();

    private final TransitionFSM transitionFsm = new TransitionFSM();

    private final TriggerFSM triggerFsm = new TriggerFSM();

    public ExecutorDefinition mrNamePattern(String pattern)
        throws DefinitionException {
        if (executorFsm.isTriggersEntered()) {
            tryBuildTrigger();
            executorFsm.exitTriggers();
        }
        if (executorFsm.isTransitionsEntered()) {
            tryBuildTransition();
            executorFsm.exitTransitions();
        }
        executor.mrNamePattern(Pattern.compile(pattern));
        executorFsm.visitMRNamePattern();
        return this;
    }

    public ExecutorDefinition transitions() throws DefinitionException {
        if (executorFsm.isTriggersEntered()) {
            tryBuildTrigger();
            executorFsm.exitTriggers();
        }
        executorFsm.enterTransitions();
        return this;
    }

    public ExecutorDefinition triggers() throws DefinitionException {
        if (executorFsm.isTransitionsEntered()) {
            tryBuildTransition();
            executorFsm.exitTransitions();
        }
        executorFsm.enterTriggers();
        return this;
    }

    public ExecutorDefinition when() throws DefinitionException {
        if (!executorFsm.isTransitionsEntered()
            && !executorFsm.isTriggersEntered()) {
            throw new InvalidSpecException(
                "`when` outside of `transitions` or `triggers`."
            );
        }
        if (executorFsm.isTransitionsEntered()) {
            if (transitionFsm.isComplete()) {
                tryBuildTransition();
            }
            transitionFsm.enterWhen();
        }
        if (executorFsm.isTriggersEntered()) {
            if (triggerFsm.isComplete()) {
                tryBuildTrigger();
            }
            triggerFsm.enterWhen();
        }
        return this;
    }

    public ExecutorDefinition mrState(MRState state)
        throws DefinitionException {
        if (!transitionFsm.isWhenEntered() && !triggerFsm.isWhenEntered()) {
            throw new InvalidSpecException("filter outside of `when`.");
        }
        filter.mrState(state);
        return this;
    }

    public ExecutorDefinition newLabel(String title)
        throws DefinitionException {
        if (!transitionFsm.isWhenEntered() && !triggerFsm.isWhenEntered()) {
            throw new InvalidSpecException("filter outside of `when`.");
        }
        filter.newLabel(title);
        return this;
    }

    public ExecutorDefinition delLabel(String title)
        throws DefinitionException {
        if (!transitionFsm.isWhenEntered() && !triggerFsm.isWhenEntered()) {
            throw new InvalidSpecException("filter outside of `when`.");
        }
        filter.delLabel(title);
        return this;
    }

    public ExecutorDefinition newReviewer() throws DefinitionException {
        if (!transitionFsm.isWhenEntered() && !triggerFsm.isWhenEntered()) {
            throw new InvalidSpecException("filter outside of `when`.");
        }
        filter.newReviewer();
        return this;
    }

    public ExecutorDefinition test(Predicate<MREvent> predicate)
        throws DefinitionException {
        if (!transitionFsm.isWhenEntered() && !triggerFsm.isWhenEntered()) {
            throw new InvalidSpecException("filter outside of `when`.");
        }
        filter.test(predicate);
        return this;
    }

    public ExecutorDefinition status(String status)
        throws DefinitionException {
        if (!executorFsm.isTransitionsEntered()) {
            throw new InvalidSpecException("`status` outside of `transitions`");
        }
        if (transitionFsm.isComplete()) {
            tryBuildTransition();
        }
        transition.status(status);
        transitionFsm.visitStatus();
        if (transitionFsm.isWhenEntered()) {
            transitionFsm.exitWhen();
        }
        return this;
    }

    public ExecutorDefinition resolution(Resolution resolution)
        throws DefinitionException {
        if (!executorFsm.isTransitionsEntered()) {
            throw new InvalidSpecException("`resolution` outside of `transitions`");
        }
        if (transitionFsm.isComplete() && transitionFsm.isResolutionVisited()) {
            tryBuildTransition();
        }
        transition.resolution(resolution);
        transitionFsm.visitResolution();
        if (transitionFsm.isWhenEntered()) {
            transitionFsm.exitWhen();
        }
        return this;
    }

    public ExecutorDefinition action(Function<MREvent, String> action)
        throws DefinitionException {
        if (!executorFsm.isTriggersEntered()) {
            throw new InvalidSpecException("`action` outside of `triggers`");
        }
        if (triggerFsm.isComplete()) {
            tryBuildTrigger();
        }
        trigger.action(action);
        triggerFsm.visitAction();
        triggerFsm.exitWhen();
        return this;
    }

    public Executor define() throws DefinitionException {
        if (!executorFsm.isComplete()) {
            throw new InvalidSpecException("Some fields missed.");
        }
        if (executorFsm.isTriggersEntered()) {
            tryBuildTrigger();
            executorFsm.exitTriggers();
        }
        if (executorFsm.isTransitionsEntered()) {
            tryBuildTransition();
            executorFsm.exitTransitions();
        }
        executor.yt(yt);
        return executor.build();
    }

    private void tryBuildTransition() throws DefinitionException {
        if (transitionFsm.isClean()) {
            return;
        }
        if (!transitionFsm.isComplete()) {
            throw new InvalidSpecException("Incomplete transition.");
        }
        transition.filter(filter.build());
        filter = Filter.builder();
        executor.transition(transition.build());
        transition = Transition.builder();
        transitionFsm.reset();
    }

    private void tryBuildTrigger() throws DefinitionException {
        if (triggerFsm.isClean()) {
            return;
        }
        if (!triggerFsm.isComplete()) {
            throw new InvalidSpecException("Incomplete transition.");
        }
        trigger.filter(filter.build());
        filter = Filter.builder();
        executor.trigger(trigger.build());
        trigger = Trigger.builder();
        triggerFsm.reset();
    }
}
