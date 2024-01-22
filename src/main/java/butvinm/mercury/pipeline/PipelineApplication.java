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

        ExecutorConfig config;
        try {
            config = ExecutorConfig.read(configFile);
        } catch (DatabindException e) {
            log.error(e.getMessage());
            digest
                .append("Bad config file: ")
                .append(e.getMessage());
            return digest.toString();
        } catch (IOException e) {
            log.error(e.getMessage());
            digest
                .append("Cannot read config file: ")
                .append(e.getMessage());
            return digest.toString();
        }

        // basic executor defined in config.yml
        log.info("Start basic executor");
        digest.append("Basic executor: ");
        Executor executor = Executor.fromConfig(yt, config);
        String result = executor.processEvent(event);
        log.info("Result: {}", result);
        digest.append(result).append("\n");

        // custom executor
        log.info("Start custom executor");
        digest.append("Custom executor: ");
        try {
            executor = customExecutor(config);
        } catch (DefinitionException e) {
            log.error(e.getMessage());
            digest
                .append("Bad executor definition.")
                .append(e.getMessage());
            return digest.toString();
        }
        result = executor.processEvent(event);
        log.info("Result: {}", result);
        digest.append(result).append("\n");

        return digest.toString();
    }

    private YTClient initYtClient(String token, String orgId) {
        return new YTClient(token, orgId);
    }

    private Executor customExecutor(ExecutorConfig config)
        throws DefinitionException {
        return Executor.definition(yt)
        .mrNamePattern(config.getMrNamePattern().pattern())
        .triggers()
            .when()
                .test(e -> true)
            .action(e -> "Hello from custom executor!")
        .define();
    }
}
