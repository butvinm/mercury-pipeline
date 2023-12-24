package butvinm.mercury.pipeline;

import kong.unirest.core.HttpResponse;
import kong.unirest.core.Unirest;
import kong.unirest.core.UnirestInstance;
import lombok.val;

/**
 * Yandex Tracker API client.
 * https://cloud.yandex.com/en-ru/docs/tracker/concepts/access
 */
public class YTClient {
    private static final String baseUrl = "https://api.tracker.yandex.net";

    private final UnirestInstance unirest;

    public YTClient(String token, String orgId) {
        this.unirest = YTClient.initUnirest(token, orgId);
    }

    private static UnirestInstance initUnirest(String token, String orgId) {
        val unirest = Unirest.primaryInstance();
        unirest.config().defaultBaseUrl(YTClient.baseUrl)
            .addDefaultHeader("Authorization", "OAuth %s".formatted(token))
            .addDefaultHeader("X-Cloud-Org-Id", orgId);
        return unirest;
    }

    public HttpResponse<String> transitIssueStatus(String issueId,
        String transitionId) {
        return unirest
            .post("/v2/issues/{issues-id}/transitions/{transition-id}/_execute")
            .routeParam("issues-id", issueId)
            .routeParam("transition-id", transitionId)
            .asString();
    }

    public void close() {
        this.unirest.close();
    }
}
