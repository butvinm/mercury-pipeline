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
import butvinm.mercury.pipeline.executor.Executor;
import butvinm.mercury.pipeline.executor.ExecutorConfig;
import butvinm.mercury.pipeline.executor.definition.exceptions.DefinitionException;
import butvinm.mercury.pipeline.models.MREvent;
import lombok.extern.slf4j.Slf4j;

@SpringBootApplication
@RestController
@Slf4j
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
            throw new ConfigFileMissedException(this.configFile);
        }

        this.yt = initYtClient(ytToken, ytOrgId);
    }

    @PostMapping("/merge-requests")
    public String mergeRequestHandler(
        @RequestBody MREvent event
    ) {
        log.info("Event: {}", event);

        var digest = new StringBuilder();

        // basic executor defined in config.yml
        digest.append("Basic executor: ");
        try {
            log.info("Start basic executor");

            var executor = loadExecutor();
            var result = executor.processEvent(event);

            log.info("Result: {}", result);
            digest.append(result);
        } catch (DatabindException e) {
            log.error(e.getMessage());
            digest
                .append("Bad config file: ")
                .append(e.getMessage());
        } catch (IOException e) {
            log.error(e.getMessage());
            digest
                .append("Cannot read config file: ")
                .append(e.getMessage());
        }
        digest.append("\n");

        // basic executor defined in config.yml
        digest.append("Custom executor: ");
        try {
            log.info("Start custom executor");

            var executor = customExecutor();
            var result = executor.processEvent(event);

            log.info("Result: {}", result);
            digest.append(result);
        } catch (DefinitionException e) {
            log.error(e.getMessage());
            digest
                .append("Bad executor definition. Verify customExecutor() function.")
                .append(e.getMessage());
        }
        digest.append("\n");

        return digest.toString();
    }

    private YTClient initYtClient(String token, String orgId) {
        return new YTClient(token, orgId);
    }

    private Executor loadExecutor() throws IOException, DatabindException {
        return Executor.fromConfig(yt, ExecutorConfig.read(configFile));
    }

    private Executor customExecutor() throws DefinitionException {
        return Executor.definition(yt).define();
    }
}
