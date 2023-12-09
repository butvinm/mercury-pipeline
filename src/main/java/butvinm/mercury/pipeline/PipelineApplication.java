package butvinm.mercury.pipeline;

import java.io.IOException;
import java.util.Arrays;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

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

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

    @PostMapping("/merge-requests")
    public String mergeRequestHandler(
        @RequestBody MREvent event
    ) {
        logger.info(event.toString());

        logger.info("Action: " + event.getObjectAttributes().getAction());
        var action = event.getObjectAttributes().getAction();
        if (action == null) {
            logger.info("Unknown action");
            return "Unknown action";
        }
        var issueId = getIssueId(event);
        switch (action) {
        case APPROVED:
            logger.info("-- (approved)");
            return "No Action (approved)";
        case CLOSE:
            logger.info("-- (closed)");
            return "No Action (closed)";
        case MERGE:
            logger.info("-- (merged)");
            return "No Action (merged)";
            // return yt.transitIssueStatus(issueId, "close").getBody();
        case UPDATE:
            var changes = event.getChanges();
            if (changes.getReviewers() != null) {
                var prevReviewers = event.getChanges().getReviewers()
                    .getPrevious();
                var currReviewers = event.getChanges().getReviewers()
                    .getCurrent();
                if (currReviewers.size() > prevReviewers.size()) {
                    logger.info("-> review");
                    return yt.transitIssueStatus(issueId, "in_review").getBody();
                }
            }
            if (changes.getLabels() != null) {
                var currLabels = event.getChanges().getLabels().getCurrent();
                if (currLabels.stream()
                    .anyMatch(l -> l.getTitle().equals("rejected"))) {
                    logger.info("-> rejected");
                    return yt.transitIssueStatus(issueId, "need_info")
                        .getBody();
                }
            }
            default:
            break;
        }
        return event.toString();
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

    private String getIssueId(MREvent event) {
        var mrTitle = event.getObjectAttributes().getTitle();
        var parts = mrTitle.split("-");
        if (parts.length < 2) {
            return null;
        }
        return String.join("-", Arrays.copyOfRange(parts, 1, parts.length));
    }
}
