package butvinm.mercury.pipeline.executor.definition;

import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.definition.exceptions.InvalidSpecException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
class TriggerFSM {
    private boolean whenVisited = false;

    private boolean whenEntered = false;

    private boolean actionVisited = false;

    public void reset() {
        whenVisited = false;
        whenEntered = false;
        actionVisited = false;
    }

    public void enterWhen() throws DefinitionException {
        if (whenVisited || whenEntered) {
            throw new InvalidSpecException("`when` visited twice.");
        }
        whenVisited = true;
        whenEntered = true;
    }

    public void exitWhen() throws DefinitionException {
        if (!whenEntered) {
            throw new InvalidSpecException("`when` exited twice.");
        }
        whenEntered = false;
    }

    public void visitAction() throws DefinitionException {
        if (actionVisited) {
            throw new DefinitionException(
                "`action` visited twice without reset."
            );
        }
        actionVisited = true;
    }

    public boolean isComplete() {
        return whenVisited && actionVisited;
    }

    public boolean isClean() {
        return !whenVisited && !actionVisited;
    }
}
