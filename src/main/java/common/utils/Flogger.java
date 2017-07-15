package common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Log Wrapper
 */
public class Flogger {
    private static final Flogger instance = new Flogger(Logger.getGlobal());

    private final Logger loggger;

    private Flogger(Logger logger) {
        this.loggger = logger;
    }

    public static Flogger getInstance() {
        return instance;
    }

    public Handle atInfo() {
        return getHandle(Level.INFO);
    }


    private Map<Level, Handle> handleMap = new HashMap<>();

    private Handle getHandle(Level level) {
        return handleMap.computeIfAbsent(level, Handle::new);
    }

    public class Handle {
        private final Level level;

        private Handle(Level level) {
            this.level = level;
        }

        public void log(String message) {
            loggger.log(level, message);
        }

        public void log(String message, Object... args) {
            loggger.log(level, String.format(message, args));
        }
    }
}
