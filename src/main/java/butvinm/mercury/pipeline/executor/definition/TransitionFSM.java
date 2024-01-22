package butvinm.mercury.pipeline.executor.definition;

import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
@EqualsAndHashCode
class TransitionFSM {
    private boolean whenVisited = false;

    private boolean statusVisited = false;

    public void reset() {
        whenVisited = false;
        statusVisited = false;
    }

    public void visitWhen() throws DefinitionException {
        if (whenVisited) {
            throw new DefinitionException(
                "`when` visited twice without reset."
            );
        }
        whenVisited = true;
    }

    public void visitStatus() throws DefinitionException {
        if (statusVisited) {
            throw new DefinitionException(
                "`status` visited twice without reset."
            );
        }
        statusVisited = true;
    }
}
