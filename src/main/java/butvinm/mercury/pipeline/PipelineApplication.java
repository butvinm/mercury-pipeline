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

import butvinm.mercury.pipeline.models.MREvent;

@SpringBootApplication
@RestController
public class PipelineApplication {
    private final Logger logger = initLogger();

    private final YTClient yt = new YTClient(
        System.getenv("YT_TOKEN"),
        System.getenv("YT_ORG_ID")
    );

    private final EventHandler handler = initHandler();

    private AppConfig config;

    private AppConfig loadConfig() {
        try {
            return AppConfig.fromYaml(new File("./config.yml"));
        } catch (IOException err) {
            throw new RuntimeException(err);
        }
    }

    private EventHandler initHandler() {
        var handler = new EventHandler();
        handler
            .when(EventFilter.of().reviewerAssigned())
            .run(this::handleReviewerAssignment);
        handler
            .when(EventFilter.of().labeled("rejected"))
            .run(this::handleMrRejection);
        return handler;
    }

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

    @PostMapping("/merge-requests")
    public String mergeRequestHandler(
        @RequestBody MREvent event
    ) {
        config = loadConfig();
        logger.info(event.toString());
        return String.join("\n", handler.handle(event));
    }

    private String handleReviewerAssignment(MREvent event) {
        var issueId = getIssueId(event);
        if (issueId.isPresent()) {
            return yt.transitIssueStatus(issueId.get(), "in_review")
                .getBody();
        }
        return "Bad Issue Id";
    }

    private String handleMrRejection(MREvent event) {
        var issueId = getIssueId(event);
        if (issueId.isPresent()) {
            return yt.transitIssueStatus(issueId.get(), "need_info").getBody();
        }
        return "Bad Issue Id";
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

    private Optional<String> getIssueId(MREvent event) {
        var pattern = Pattern.compile(config.getMrNamePattern());
        var matcher = pattern.matcher(event.getObjectAttributes().getTitle());
        if (matcher.find()) {
            return Optional.of(matcher.group("issueId"));
        }
        return Optional.empty();
    }
}
