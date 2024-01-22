package butvinm.mercury.pipeline.executor.filter;

import java.util.function.Predicate;

import butvinm.mercury.pipeline.models.MREvent;
import lombok.Data;

@Data
class TestNewReviewer implements Predicate<MREvent> {
    @Override
    public boolean test(MREvent event) {
        var reviewers = event.getChanges().getReviewers();
        if (reviewers == null) {
            return false;
        }
        return reviewers.getPrevious().size() < reviewers.getCurrent().size();
    }
}
