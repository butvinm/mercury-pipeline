package butvinm.mercury.pipeline.providers.yt.ytapi;

/**
 * Endpoints of Yandex Tracker API.
 *
 * See <a href="https://cloud.yandex.com/en-ru/docs/tracker/about-api">API
 * reference</a> for details.
 */
public class Endpoints {
    public static final String BASE_PREFIX = "/v2";

    public static class Users {
        public static final String MYSELF = BASE_PREFIX + "/myself";
    }
}
