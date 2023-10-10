package butvinm.mercury.pipeline.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.jsontype.SubtypeResolver;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import io.vavr.collection.List;
import io.vavr.control.Try;

/**
 * Config reader.
 *
 * Parse {@link PipelinConfig} from YAML file.
 */
public class ConfigReader {
    /** Jackson object mapper. */
    private final ObjectMapper mapper;

    /**
     * Create Config Reader for specified plugins.
     *
     * @param plugins {@link PluginConfig} subtypes classes.
     */
    public ConfigReader(List<Class<? extends PluginConfig>> plugins) {
        this.mapper = this.intiMapper(plugins);
    }

    /**
     * Parse pipeline config from content of YAML file and map to
     * {@link PipelineConfig}.
     *
     * @param configContent Content of YAML file with config.
     * @return PipelineConfig instance or an error.
     */
    public Try<PipelineConfig> parseConfig(String configContent) {
        return Try.of(() -> this.mapper.readValue(
            configContent,
            PipelineConfig.class
        ));
    }

    /**
     * Create {@link ObjectMapper} instance that can map plugins configurations
     * to their structures.
     *
     * @param plugins {@link PluginConfig} subtypes classes.
     * @return {@link ObjectMapper} for specified plugins.
     */
    private ObjectMapper intiMapper(
        List<Class<? extends PluginConfig>> plugins
    ) {
        var mapper = new ObjectMapper(new YAMLFactory());

        SubtypeResolver subtypeResolver = mapper.getSubtypeResolver();
        subtypeResolver.registerSubtypes(plugins.toJavaArray(Class[]::new));
        mapper.setSubtypeResolver(subtypeResolver);

        mapper.findAndRegisterModules();
        return mapper;
    }
}
