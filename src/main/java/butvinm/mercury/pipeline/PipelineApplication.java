package butvinm.mercury.pipeline;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.regex.Pattern;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;

import butvinm.mercury.pipeline.handler.EventHandler;
import butvinm.mercury.pipeline.handler.EventHandlerConfig;
import butvinm.mercury.pipeline.models.MREvent;

@SpringBootApplication
@RestController
public class PipelineApplication {
    private final Logger logger = initLogger();
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
    private final File configFile = new File("/config.yml");
    private final YTClient yt = new YTClient(
        System.getenv("YT_TOKEN"),
        System.getenv("YT_ORG_ID")
    );

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

    @PostMapping("/merge-requests")
    public String mergeRequestHandler(
        @RequestBody MREvent event
    ) {
        logger.info(event.toString());
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

    private EventHandler loadExecutor() throws IOException, DatabindException {
        var config = mapper.readValue(configFile, EventHandlerConfig.class);
        return EventHandler.fromConfig(yt, config);
    }

    private Logger initLogger() {
        var logger = Logger.getLogger("main");
        try {
            var fileHandler = new FileHandler("/tmp/logs/logs.log");
            logger.addHandler(fileHandler);
            var formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            return logger;
        } catch (SecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
