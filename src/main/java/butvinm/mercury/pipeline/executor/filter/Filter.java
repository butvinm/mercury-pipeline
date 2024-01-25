package butvinm.mercury.pipeline.executor.filter;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import butvinm.mercury.pipeline.gitlab.models.MREvent;
import butvinm.mercury.pipeline.gitlab.models.MRState;
import lombok.Data;

@Data
public class Filter implements Predicate<MREvent> {
    private final List<Predicate<MREvent>> predicates;

    public static FilterBuilder builder() {
        return new FilterBuilder();
    }

    public static Filter fromConfig(FilterConfig config) {
        var builder = Filter.builder();
        if (config.getNewLabel() != null) {
            builder.newLabel(config.getNewLabel());
        }
        if (config.getDeletedLabel() != null) {
            builder.delLabel(config.getDeletedLabel());
        }
        if (config.getNewReviewer() != null && config.getNewReviewer()) {
            builder.newReviewer();
        }
        if (config.getMrState() != null) {
            builder.mrState(config.getMrState());
        }
        return builder.build();
    }

    @Override
    public boolean test(MREvent event) {
        return predicates.stream().allMatch(predicate -> predicate.test(event));
    }

    public static class FilterBuilder {
        private final List<Predicate<MREvent>> predicates = new ArrayList<>();

        public FilterBuilder mrState(MRState state) {
            predicates.add(new TestMRState(state));
            return this;
        }

        public FilterBuilder newLabel(String title) {
            predicates.add(new TestNewLabel(title));
            return this;
        }

        public FilterBuilder delLabel(String title) {
            this.predicates.add(new TestDelLabel(title));
            return this;
        }

        public FilterBuilder newReviewer() {
            this.predicates.add(new TestNewReviewer());
            return this;
        }

        public FilterBuilder test(Predicate<MREvent> predicate) {
            this.predicates.add(predicate);
            return this;
        }

        public Filter build() {
            return new Filter(predicates);
        }
    }
}
