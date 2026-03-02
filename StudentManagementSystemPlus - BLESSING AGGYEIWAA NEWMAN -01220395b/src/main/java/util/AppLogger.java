package util;

import java.io.IOException;
import java.util.logging.*;

public class AppLogger {
    private static final Logger logger = Logger.getLogger("AJSystemLog");
    private static final String LOG_FILE = "data/aj_system.log";

    static {
        try {
            new java.io.File("data").mkdirs();
            // Avoid adding duplicate handlers if class is loaded multiple times
            if (logger.getHandlers().length == 0) {
                FileHandler fh = new FileHandler(LOG_FILE, true);
                fh.setFormatter(new SimpleFormatter());
                logger.addHandler(fh);
                logger.setUseParentHandlers(false); // Set to true if you want console output too
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void logInfo(String message) { logger.info(message); }
    public static void logError(String message) { logger.severe(message); }
}