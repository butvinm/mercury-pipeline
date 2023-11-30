package butvinm.mercury.pipeline;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import butvinm.mercury.pipeline.models.MREvent;
import lombok.val;

@SpringBootApplication
@RestController
public class PipelineApplication {
    private final Logger logger = initLogger();

    public static void main(String[] args) {
        SpringApplication.run(PipelineApplication.class, args);
    }

    @PostMapping("/merge-requests")
    public String mergeRequestHandler(
        @RequestBody MREvent event
    ) {
        logger.info(event.toString());
        val prevReviewers = event.getChanges().getReviewers().getPrevious();
        val currReviewers = event.getChanges().getReviewers().getPrevious();
        if (currReviewers.equals(prevReviewers)) {
            return "New Reviewer!";
        }
        return event.toString();
    }

    private Logger initLogger() {
        val logger = Logger.getLogger("main");
        try {
            val fileHandler = new FileHandler("/tmp/logs/logs.log");
            logger.addHandler(fileHandler);
            val formatter = new SimpleFormatter();
            fileHandler.setFormatter(formatter);
            return logger;
        } catch (SecurityException | IOException e) {
            throw new RuntimeException(e);
        }
    }
}
