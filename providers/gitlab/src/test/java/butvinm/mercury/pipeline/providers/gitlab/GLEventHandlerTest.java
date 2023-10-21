package butvinm.mercury.pipeline.providers.gitlab;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Path;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import com.fasterxml.jackson.databind.ObjectMapper;

import butvinm.mercury.pipeline.core.events.common.MREvent;
import io.vavr.collection.Map;
import io.vavr.collection.Stream;
import io.vavr.jackson.datatype.VavrModule;

public class GLEventHandlerTest {
    private final static ObjectMapper mapper = initMapper();

    private GLEventHandler eventHandler;

    private final static String notCiError = "You are not in GitLab CI environment.";

    private final static String notMRError = "Can process only Merge Requests.";

    private final static String parseError = "IllegalArgumentException";

    @BeforeEach
    public void setUp() {
        eventHandler = new GLEventHandler();
    }

    private static ObjectMapper initMapper() {
        var mapper = new ObjectMapper();
        mapper.registerModule(new VavrModule());
        return mapper;
    }

    private static Map<String, String> readEnv(String envFile)
        throws IOException {
        var filePath = Path.of(
            GLEventHandlerTest.class.getResource(envFile).getPath()
        );
        return mapper.readValue(filePath.toFile(), Map.class);
    }

    private static Stream<Arguments> getTestEnvs() throws IOException {
        return Stream.of(
            Arguments.of(readEnv("mr_env_correct.json"), true, ""),
            Arguments.of(readEnv("mr_env_empty.json"), false, notCiError),
            Arguments.of(readEnv("mr_env_incorrect.json"), false, parseError),
            Arguments.of(readEnv("mr_env_not_ci.json"), false, notCiError),
            Arguments.of(readEnv("mr_env_not_mr.json"), false, notMRError),
            Arguments.of(readEnv("mr_env_undef.json"), false, parseError)
        );
    }

    @ParameterizedTest
    @MethodSource("getTestEnvs")
    public void test(Map<String, String> env, Boolean success, String error) {
        var result = eventHandler.handleEvent(env);
        assertEquals(success, result.isRight());
        if (success) {
            var event = (MREvent) result.get();
            // Expect 2 values there, so do not forget to check test files
            assertEquals(2, event.getAssignees().size());
        } else {
            assertTrue(result.getLeft().contains(error));
        }
    }
}
