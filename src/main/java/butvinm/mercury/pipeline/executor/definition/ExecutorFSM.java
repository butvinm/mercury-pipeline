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

    public void visitMRNamePattern() throws DefinitionException {
        if (mrNamePatternVisited) {
            throw new InvalidSpecException("`mrNamePattern` visited twice.");
        }
        mrNamePatternVisited = true;
    }

    public void visitTransitions() throws DefinitionException {
        if (transitionsVisited) {
            throw new InvalidSpecException("`transitions` visited twice.");
        }
        transitionsVisited = true;
    }
}
