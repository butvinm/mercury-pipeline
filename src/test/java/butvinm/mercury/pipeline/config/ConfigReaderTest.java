package butvinm.mercury.pipeline.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import butvinm.mercury.pipeline.config.utils.TestPluginConfig1;
import butvinm.mercury.pipeline.config.utils.TestPluginConfig2;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

public class ConfigReaderTest {
    private ConfigReader configReader;

    @BeforeEach
    public void setUp() {
        List<Class<? extends PluginConfig>> plugins = List.of(
            TestPluginConfig1.class,
            TestPluginConfig2.class
        );
        configReader = new ConfigReader(plugins);
    }

    private static Stream<Arguments> getValidConfigFiles() {
        return Stream.of(
            Arguments.of("test-config1.yml", 1),
            Arguments.of("test-config2.yml", 1),
            Arguments.of("test-config3.yml", 2),
            Arguments.of("test-config4.yml", 0)
        );
    }

    @ParameterizedTest
    @MethodSource("getValidConfigFiles")
    public void testParseValidConfigFiles(
        String configResource,
        Integer expectedPluginsCount
    ) {
        var result = configReader.parseConfig(
            this.getResourcePath(configResource)
        );
        assertTrue(result.isSuccess());

        PipelineConfig pipelineConfig = result.get();
        assertEquals(expectedPluginsCount,
            pipelineConfig.getPlugins().length());
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid-config.yml"})
    public void testParseInvalidConfigFile(String configFileResource) {
        // Parse the config file
        var result = configReader.parseConfig(
            this.getResourcePath(configFileResource)
        );
        assertTrue(result.isFailure());
        assertTrue(result.getCause() instanceof IOException);
    }

    private Path getResourcePath(String resourceName) {
        return Path.of(getClass().getResource(resourceName).getPath());
    }
}
