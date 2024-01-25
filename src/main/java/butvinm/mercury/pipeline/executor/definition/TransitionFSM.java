package butvinm.mercury.pipeline.executor.definition;

import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.executor.definition.exceptions.InvalidSpecException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
class TransitionFSM {
    private boolean whenVisited = false;

    private boolean whenEntered = false;

    private boolean statusVisited = false;

    private boolean resolutionVisited = false;

    public void reset() {
        whenVisited = false;
        whenEntered = false;
        statusVisited = false;
        resolutionVisited = false;
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

    public void visitStatus() throws DefinitionException {
        if (statusVisited) {
            throw new InvalidSpecException(
                "`status` visited twice without reset."
            );
        }
        statusVisited = true;
    }

    public void visitResolution() throws DefinitionException {
        if (resolutionVisited) {
            throw new InvalidSpecException(
                "`resolution` visited twice without reset."
            );
        }
        resolutionVisited = true;
    }

    public boolean isComplete() {
        return whenVisited && statusVisited;
    }

    public boolean isClean() {
        return !whenVisited && !statusVisited;
    }
}
