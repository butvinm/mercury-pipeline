package butvinm.mercury.pipeline.core.config;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import butvinm.mercury.pipeline.core.config.utils.TestPluginConfig1;
import butvinm.mercury.pipeline.core.config.utils.TestPluginConfig2;
import io.vavr.collection.HashSet;
import io.vavr.collection.List;
import io.vavr.collection.Set;
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
            Arguments.of(
                getResourceContent("test-config1.yml"),
                HashSet.of("plugin1"),
                true
            ),
            Arguments.of(
                getResourceContent("test-config2.yml"),
                HashSet.of("plugin2"),
                true
            ),
            Arguments.of(
                getResourceContent("test-config3.yml"),
                HashSet.of("plugin1", "plugin2"),
                true
            ),
            Arguments.of(
                getResourceContent("test-config4.yml"),
                HashSet.empty(),
                true
            ),
            Arguments.of(
                getResourceContent("invalid-config.yml"),
                HashSet.empty(),
                false
            )
        );
    }

    @ParameterizedTest
    @MethodSource("getValidConfigFiles")
    public void testParseValidConfigFiles(
        String configContent,
        Set<String> expectedPlugins,
        Boolean success
    ) {
        var result = configReader.parseConfig(configContent);
        assertEquals(success, result.isSuccess());

        if (success) {
            PipelineConfig pipelineConfig = result.get();
            assertEquals(
                expectedPlugins,
                pipelineConfig.getPlugins().map(p -> p.getName()).toSet()
            );
        }
    }

    private static String getResourceContent(String resourceName)
        throws IOException {
        var resourcePath = Path.of(
            ConfigReaderTest.class.getResource(resourceName).getPath()
        );
        return Files.readString(resourcePath);
    }
}
