package butvinm.mercury.pipeline.executor.definition;

import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.definition.exceptions.InvalidSpecException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
class ExecutorFSM {
    private boolean mrNamePatternVisited = false;

    private boolean transitionsVisited = false;

    private boolean transitionsEntered = false;

    private boolean triggersVisited = false;

    private boolean triggersEntered = false;

    public void visitMRNamePattern() throws DefinitionException {
        if (mrNamePatternVisited) {
            throw new InvalidSpecException("`mrNamePattern` visited twice.");
        }
        mrNamePatternVisited = true;
    }

    public void enterTransitions() throws DefinitionException {
        if (transitionsVisited || transitionsEntered) {
            throw new InvalidSpecException("`transitions` defined twice.");
        }
        if (triggersEntered) {
            throw new DefinitionException(
                "`transitions` nested in `triggers`."
            );
        }
        transitionsEntered = true;
        transitionsVisited = true;
    }

    public void exitTransitions() throws DefinitionException {
        if (!transitionsEntered) {
            throw new DefinitionException("`transitions` exited twice.");
        }
        transitionsEntered = false;
    }

    public void enterTriggers() throws DefinitionException {
        if (triggersVisited || triggersEntered) {
            throw new InvalidSpecException("`triggers` defined twice.");
        }
        if (transitionsEntered) {
            throw new DefinitionException(
                "`triggers` nested in `transitions`.");
        }
        triggersEntered = true;
        triggersVisited = true;
    }

    public void exitTriggers() throws DefinitionException {
        if (!triggersEntered) {
            throw new DefinitionException("`triggers` exited twice.");
        }
        triggersEntered = false;
    }

    public boolean isComplete() {
        return mrNamePatternVisited && (transitionsVisited || triggersVisited);
    }
}
