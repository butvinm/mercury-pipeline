package butvinm.mercury.pipeline.providers.yt.ytapi;

import io.vavr.control.Try;
import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import lombok.val;

/**
 * Yandex Tracker API client.
 *
 * Provide access to the YT API via OAuth.
 *
 * See <a href="https://cloud.yandex.com/en-ru/docs/tracker/concepts/access">API
 * Docs</a> for reference to learn how to get access token.
 */
public class YTClient {
    /** Url of Yandex Tracker API. */
    private static final String baseUrl = "https://api.tracker.yandex.net";

    /** Configured {@link UnirestInstance}. */
    private final UnirestInstance unirest;

    /**
     * Create YTClient with pre-configured {@link UnirestInstance} instance.
     *
     * @param token OAuth token.
     * @param orgId YT organization ID. You can find it on organization
     *              management page.
     */
    public YTClient(String token, String orgId) {
        this.unirest = YTClient.initUnirest(token, orgId);
    }

    /**
     * Create {@link UnirestInstance} with auth headers and base url.
     *
     * @param token OAuth token.
     * @param orgId YT organization ID.
     * @return Unirest for requests to YT API.
     */
    private static UnirestInstance initUnirest(String token, String orgId) {
        val unirest = Unirest.primaryInstance();
        unirest.config().defaultBaseUrl(YTClient.baseUrl)
            .addDefaultHeader("Authorization", "OAuth %s".formatted(token))
            .addDefaultHeader("X-Cloud-Org-Id", orgId);
        return unirest;
    }

    /**
     * Send simple request to YT API.
     *
     * Can be used to verify credentials.
     *
     * @return Try of http response. Check your token if response code is 401.
     */
    public Try<HttpResponse<String>> testConnection() {
        return Try.of(
            () -> this.unirest.get(Endpoints.Users.MYSELF).asString()
        );
    }

    /**
     * Close Unirest connection.
     */
    public void close() {
        this.unirest.close();
    }
}
