package butvinm.mercury.pipeline.exceptions;

import java.io.File;

public class ConfigFileMissedException extends Exception {
    public ConfigFileMissedException(File configFile) {
        super("Config file does not exist at specified path: %s".formatted(
            configFile
        ));
    }
}
