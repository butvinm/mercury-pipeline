package butvinm.mercury.pipeline.providers.gitlab.utils;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.module.SimpleModule;

import io.vavr.collection.List;
import io.vavr.collection.Map;
import io.vavr.control.Try;
import io.vavr.jackson.datatype.VavrModule;

/**
 * Utility to convert map with environment variables from GitLab CI to specific
 * structure.
 *
 * It utilize Jackson {@link ObjectMapper}, but prepare variable before parsing
 * and handle GL-specific variables deserialization.
 */
public class GLEnvMapper {
    /**
     * Jackson ObjectMapper configured for deal with Vavr and GL envs.
     */
    private final ObjectMapper mapper;

    /**
     * Prefix to remove from env names.
     *
     * Handy to transform env like "CI_ABC_DEF" to field "abcDef".
     */
    private final String envPrefix;

    /**
     * Construct mapper with specific env prefix.
     *
     * @param envPrefix Prefix to remove from env names.
     */
    public GLEnvMapper(String envPrefix) {
        super();
        this.mapper = initMapper();
        this.envPrefix = envPrefix;
    }

    /**
     * Construct mapper without prefix trimming.
     */
    public GLEnvMapper() {
        this("");
    }

    /**
     * Convert map of GitLab CI environment variables to object.
     *
     * @param <T>  Target structure typevar.
     * @param env  Map with env variables.
     * @param type Target structure class.
     * @return Parsed object or failure.
     */
    public <T> Try<T> convertEnv(Map<String, String> env, Class<T> type) {
        var normalizedEnv = env.mapKeys(
            k -> toCamelCase(k.replaceFirst("^" + this.envPrefix, ""))
        );
        return Try.of(() -> this.mapper.convertValue(normalizedEnv, type));
    }

    /**
     * Init Jackson {@link ObjectMapper} configured for Vavr types and GitlLab
     * environment varaibles formats.
     *
     * @return Prepared object mapper.
     */
    private ObjectMapper initMapper() {
        var mapper = new ObjectMapper();
        // Allow extra params be ignored
        mapper.configure(
            DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES,
            false
        );
        // WARNING: order of modules registration matters
        // Add Vavr types support
        mapper.registerModule(new VavrModule());
        // Add custom deserialize for comma-separated lists
        var module = new SimpleModule();
        module.addDeserializer(List.class, new ListDeserializer());
        mapper.registerModule(module);
        return mapper;
    }

    /**
     * Transform name in SNAKE_CASE to camelCase.
     *
     * Used to get field names form env names.
     *
     * @param snakeCaseString Env name in SNAKE_CASE.
     * @return Field name in camelCase.
     */
    private String toCamelCase(String snakeCaseString) {
        var builder = new StringBuilder();

        // Trim leading underscores
        snakeCaseString = snakeCaseString.replaceFirst("^_+", "");

        var chars = snakeCaseString.toCharArray();
        Boolean capitalizeNext = false;
        for (char c : chars) {
            if (c == '_') {
                capitalizeNext = true;
            } else {
                if (capitalizeNext) {
                    builder.append(Character.toUpperCase(c));
                    capitalizeNext = false;
                } else {
                    builder.append(Character.toLowerCase(c));
                }
            }
        }
        return builder.toString();
    }

    /**
     * Deserializer for comma-separated string lists.
     */
    private class ListDeserializer extends StdDeserializer<List<String>> {
        /**
         * Construct deserializer.
         */
        public ListDeserializer() {
            super(List.class);
        }

        /**
         * Parse comma-separated values.
         */
        @Override
        public List<String> deserialize(
            JsonParser parser,
            DeserializationContext context
        ) throws IOException, JsonProcessingException {
            String value = parser.getValueAsString();
            if (value == null) {
                throw new IOException("Can not parse string.");
            }
            return List.of(value.split(","));
        }
    }
}
