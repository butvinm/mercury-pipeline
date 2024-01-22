package butvinm.mercury.pipeline.executor.filter;

import java.util.function.Predicate;

import butvinm.mercury.pipeline.models.MREvent;
import lombok.Data;

@Data
class TestDelLabel implements Predicate<MREvent> {
    private final String title;

    @Override
    public boolean test(MREvent event) {
        var labels = event.getChanges().getLabels();
        if (labels == null) {
            return false;
        }
        var isNew = labels.getPrevious().size() > labels.getCurrent().size();
        var titleMatch = labels.getCurrent().stream()
            .anyMatch(l -> l.getTitle().equals(title));
        return isNew && titleMatch;
    }
}
