package butvinm.mercury.bot;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import butvinm.mercury.bot.models.PipelineEvent;
import butvinm.mercury.bot.models.PipelineStatus;

@SpringBootApplication
@RestController
public class BotApplication {
    private final Logger logger = initLogger();

    public static void main(String[] args) {
        SpringApplication.run(BotApplication.class, args);
    }

    @PostMapping("/pipelines")
    public String mergeRequestHandler(
        @RequestBody PipelineEvent event
    ) {
        logger.info(event.toString());
        var eventStatus = event.getObjectAttributes().getStatus();
        if (eventStatus.equals(PipelineStatus.SUCCESS)) {
            return handleSuccessPipeline(event);
        }
        return "none";
    }

    private String handleSuccessPipeline(PipelineEvent event) {
        return "meow";
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
