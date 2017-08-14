package common.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.*;

/**
 * Log Wrapper to standardise printing messages
 */
public class Flogger {
    private static final Flogger instance = new Flogger(Logger.getGlobal());

    private final Logger loggger;

    private Flogger(Logger logger) {
        this.loggger = logger;
        logger.setUseParentHandlers(false);
        logger.addHandler(new DualConsoleHandler());
    }

    public static Flogger getInstance() {
        return instance;
    }

    public Handle atInfo() {
        return getHandle(Level.INFO);
    }

    public Handle atError() {
        return getHandle(Level.SEVERE);
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

    private static class DualConsoleHandler extends StreamHandler {
        private final StreamHandler stderrHandler = new ConsoleHandler();
        private final StreamHandler stdoutHandler = new StreamHandler(System.out, new SimpleFormatter());

        @Override
        public void publish(LogRecord record) {
            if (record.getLevel().intValue() <= Level.INFO.intValue()) {
                stdoutHandler.publish(record);
                stdoutHandler.flush();
            } else {
                stderrHandler.publish(record);
                stderrHandler.flush();
            }
        }
    }
}
