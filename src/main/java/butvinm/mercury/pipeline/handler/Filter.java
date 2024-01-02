package butvinm.mercury.pipeline.handler;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import butvinm.mercury.pipeline.models.Action;
import butvinm.mercury.pipeline.models.MREvent;
import lombok.Data;

@Data(staticConstructor = "of")
public class Filter implements Predicate<MREvent> {
    private final List<Predicate<MREvent>> predicates;

    public static Filter of() {
        return new Filter(new ArrayList<>());
    }

    public static Filter fromConfig(FilterConfig config) {
        var filter = Filter.of();
        if (config.getNewLabel() != null) {
            filter.newLabel(config.getNewLabel());
        }
        if (config.getDeletedLabel() != null) {
            filter.delLabel(config.getDeletedLabel());
        }
        if (config.getNewReviewer() != null && config.getNewReviewer()) {
            filter.newReviewer();
        }
        if (config.getMrState() != null) {
            filter.mrState(config.getMrState());
        }
        return filter;
    }

    public boolean test(MREvent event) {
        return predicates.stream().allMatch(predicate -> predicate.test(event));
    }

    public Filter mrState(Action status) {
        this.predicates.add(
            event -> {
                var action = event.getObjectAttributes().getAction();
                return action != null && action.equals(status);
            }
        );
        return this;
    }

    public Filter newLabel(String labelTitle) {
        this.predicates.add(
            event -> {
                var labels = event.getChanges().getLabels();
                return labels != null &&
                    labels.getPrevious().size() < labels.getCurrent().size() &&
                    labels.getCurrent().stream()
                        .anyMatch(label -> label.getTitle().equals(labelTitle));
            }
        );
        return this;
    }

    public Filter delLabel(String labelTitle) {
        this.predicates.add(
            event -> {
                var labels = event.getChanges().getLabels();
                return labels != null &&
                    labels.getPrevious().size() > labels.getCurrent().size() &&
                    labels.getPrevious().stream()
                        .anyMatch(label -> label.getTitle().equals(labelTitle));
            }
        );
        return this;
    }

    public Filter newReviewer() {
        this.predicates.add(
            event -> {
                var reviewers = event.getChanges().getReviewers();
                return reviewers != null &&
                    reviewers.getPrevious().size() < reviewers.getCurrent()
                        .size();
            }
        );
        return this;
    }
}
