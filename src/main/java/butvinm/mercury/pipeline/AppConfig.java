package butvinm.mercury.pipeline;

import java.io.File;
import java.io.IOException;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import lombok.Builder;
import lombok.Data;
import lombok.extern.jackson.Jacksonized;

@Data
@Jacksonized
@Builder
public class AppConfig {
    @JsonProperty("mr_name_pattern")
    private final String mrNamePattern;

    public static AppConfig fromYaml(File file) throws IOException {
        var mapper = new ObjectMapper(new YAMLFactory());
        return mapper.readValue(file, AppConfig.class);
    }
}
