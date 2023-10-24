package butvinm.mercury.pipeline.providers.gitlab;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import io.vavr.collection.CharSeq;
import io.vavr.collection.List;
import io.vavr.collection.Stream;

public class GLEnvMapperTest {
    public String toCamelCase(String snakeCaseString) {
        final var parts = List.of(snakeCaseString.toLowerCase().split("_+"));
        return parts.headOption().getOrElse("")
            .concat(
                parts.drop(1)
                    .map(s -> CharSeq.of(s).capitalize())
                    .mkString()
            );
    }

    public static Stream<Arguments> getTestStrings() {
        return Stream.of(
            Arguments.of("my_variable_name", "myVariableName"),
            Arguments.of("CONSTANT_VALUE", "constantValue"),
            Arguments.of("long____var", "longVar"),
            Arguments.of("_", ""),
            Arguments.of("", "")
        );
    }

    @ParameterizedTest
    @MethodSource("getTestStrings")
    public void testToCamelCase(String snakeCaseString, String expected) {
        assertEquals(expected, toCamelCase(snakeCaseString));
    }
}
