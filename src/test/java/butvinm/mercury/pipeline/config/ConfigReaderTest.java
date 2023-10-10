package butvinm.mercury.pipeline.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

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

    private static Stream<Arguments> getValidConfigFiles() throws IOException {
        return Stream.of(
            Arguments.of(getResourceContent("test-config1.yml"), 1),
            Arguments.of(getResourceContent("test-config2.yml"), 1),
            Arguments.of(getResourceContent("test-config3.yml"), 2),
            Arguments.of(getResourceContent("test-config4.yml"), 0)
        );
    }

    @ParameterizedTest
    @MethodSource("getValidConfigFiles")
    public void testParseValidConfigFiles(
        String configContent,
        Integer expectedPluginsCount
    ) {
        var result = configReader.parseConfig(configContent);
        assertTrue(result.isSuccess());

        PipelineConfig pipelineConfig = result.get();
        assertEquals(expectedPluginsCount,
            pipelineConfig.getPlugins().length());
    }

    private static Stream<Arguments> getInvalidConfigFiles()
        throws IOException {
        return Stream.of(
            Arguments.of(getResourceContent("invalid-config.yml"))
        );
    }

    @ParameterizedTest
    @MethodSource("getInvalidConfigFiles")
    public void testParseInvalidConfigFile(String configContent) {
        var result = configReader.parseConfig(configContent);
        assertTrue(result.isFailure());
        assertTrue(result.getCause() instanceof IOException);
    }

    private static String getResourceContent(String resourceName)
        throws IOException {
        var resourcePath = Path.of(
            ConfigReaderTest.class.getResource(resourceName).getPath()
        );
        return Files.readString(resourcePath);
    }
}
