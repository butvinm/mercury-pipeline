package butvinm.mercury.pipeline.providers.yt.ytapi;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Objects;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import lombok.Cleanup;
import lombok.val;

public class YTClientTest {
    private String token;

    private String orgId;

    @BeforeEach
    public void initCredentials() {
        this.token = Objects.requireNonNull(
            System.getProperty("ytapi.token")
        );
        this.orgId = Objects.requireNonNull(
            System.getProperty("ytapi.orgId")
        );
    }

    @Test
    @EnabledIfSystemProperty(named = "integrationTests", matches = "true")
    public void test() {
        @Cleanup
        val client = new YTClient(this.token, this.orgId);
        val result = client.testConnection();
        assertTrue(result.isSuccess());
        val response = result.get();
        assertEquals(200, response.getStatus());
        try {
            client.close();
        } catch (Exception err) {
        }
    }
}
