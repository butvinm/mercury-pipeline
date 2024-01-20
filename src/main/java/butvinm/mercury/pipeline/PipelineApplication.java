package butvinm.mercury.pipeline;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DatabindException;

import butvinm.mercury.pipeline.exceptions.ConfigFileMissedException;
import butvinm.mercury.pipeline.exceptions.ShareDirMissedException;
import butvinm.mercury.pipeline.handler.EventHandler;
import butvinm.mercury.pipeline.handler.EventHandlerConfig;
import butvinm.mercury.pipeline.models.MREvent;

@SpringBootApplication
@RestController
public class PipelineApplication {
    private final File shareDir;

    private final File configFile;

    private final YTClient yt;

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

    public PipelineApplication(
        @Value("${share}") Path shareDir,
        @Value("${config}") Path configFile,
        @Value("${yt.token}") String ytToken,
        @Value("${yt.orgId}") String ytOrgId
    ) throws ShareDirMissedException, ConfigFileMissedException, IOException {
        this.shareDir = shareDir.toFile();
        if (!this.shareDir.exists()) {
            throw new ShareDirMissedException(this.shareDir);
        }

        this.configFile = shareDir.resolve(configFile).toFile();
        if (!this.configFile.exists()) {
            throw new ConfigFileMissedException(this.configFile );
        }

        this.yt = initYtClient(ytToken, ytOrgId);
    }

    @PostMapping("/merge-requests")
    public String mergeRequestHandler(
        @RequestBody MREvent event
    ) {
        try {
            var executor = loadExecutor();
            var result = executor.processEvent(event);
            if (result.isEmpty()) {
                return "Processing failed";
            }
            return result.get();
        } catch (DatabindException e) {
            return "Bad config file: %s".formatted(e.getMessage());
        } catch (IOException e) {
            return "Cannot read config file: %s".formatted(e.getMessage());
        }
    }

    private YTClient initYtClient(String token, String orgId) {
        return new YTClient(token, orgId);
    }

    private EventHandler loadExecutor() throws IOException, DatabindException {
        return EventHandler.fromConfig(yt, EventHandlerConfig.read(configFile));
    }
}
