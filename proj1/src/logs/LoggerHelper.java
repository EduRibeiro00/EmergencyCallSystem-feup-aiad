package logs;

import java.io.IOException;
import java.util.logging.*;

public class LoggerHelper {
    private final static Logger LOGGER = Logger.getLogger(Logger.GLOBAL_LOGGER_NAME);
    static private FileHandler fileTxt;
    static private SimpleFormatter formatterTxt;

    public static void setup() throws IOException {
        // suppress the logging output to the console
        Logger rootLogger = Logger.getLogger("");
        Handler[] handlers = rootLogger.getHandlers();
        if (handlers[0] instanceof ConsoleHandler) {
            rootLogger.removeHandler(handlers[0]);
        }

        LOGGER.setLevel(Level.INFO);
        fileTxt = new FileHandler("logs.txt");

        // create a TXT formatter
        formatterTxt = new SimpleFormatter();
        fileTxt.setFormatter(formatterTxt);
        LOGGER.addHandler(fileTxt);
    }

    public static void logInfo(String text) {
        LOGGER.info(text);
    }

    public static void logError(String text) {
        LOGGER.severe(text);
    }

    public static void logWarning(String text) {
        LOGGER.warning(text);
    }
}
