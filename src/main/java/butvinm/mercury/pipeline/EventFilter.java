package butvinm.mercury.pipeline;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import butvinm.mercury.pipeline.models.Action;
import butvinm.mercury.pipeline.models.MREvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor(staticName = "of")
public class EventFilter {
    private final List<Predicate<MREvent>> predicates;

    public Boolean test(MREvent event) {
        return predicates.stream().allMatch(predicate -> predicate.test(event));
    }

    public static EventFilter of() {
        return new EventFilter(new ArrayList<>());
    }

    public EventFilter action(Action actionType) {
        this.predicates.add(
            event -> {
                var action = event.getObjectAttributes().getAction();
                return action != null && action.equals(actionType);
            }
        );
        return this;
    }

    public EventFilter labeled(String labelTitle) {
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

    public EventFilter unlabeled(String labelTitle) {
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

    public EventFilter reviewerAssigned() {
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
