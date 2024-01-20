package butvinm.mercury.pipeline.handler;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.regex.Pattern;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class EventHandlerConfig {
    @JsonProperty("mr_name_pattern")
    private final Pattern mrNamePattern;

    @JsonProperty("transitions")
    private final List<TransitionConfig> transitions;

    public static EventHandlerConfig read(File file)
    throws IOException, DatabindException {
        var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, EventHandlerConfig.class);
    }
}
