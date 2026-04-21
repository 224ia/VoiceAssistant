package Util;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class Logger {
    private static final Path LOG_FILE_PATH = Path.of("log.txt");
    static {
        try {
            Files.writeString(LOG_FILE_PATH, "");
        } catch (IOException e) {
            System.err.println("Failed to create log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static final DateTimeFormatter TIME_FORMAT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS");

    private static boolean debugMode = true;

    public static void setDebugMode(boolean enabled) {
        debugMode = enabled;
    }

    private static void log(Object message, LogLevel logLevel) {
        if (message == null || logLevel == null) {
            throw new IllegalArgumentException("Message and log level can't be null");
        }

        String output = String.format("[%s] [%s] %s%n",
                LocalTime.now().format(TIME_FORMAT), logLevel.levelName, message);

        if (logLevel != LogLevel.ERROR) {
            System.out.println(output);
        } else {
            System.err.println(output);
        }

        try {
            Files.writeString(LOG_FILE_PATH, output, StandardOpenOption.APPEND);
        } catch (IOException e) {
            System.err.println("Failed to write into log file: " + e.getMessage());
            e.printStackTrace();
        }
    }

    public static void info(Object message) {
        log(message, LogLevel.INFO);
    }

    public static void warn(Object message) {
        log(message, LogLevel.WARN);
    }

    public static void error(Object message) {
        log(message, LogLevel.ERROR);
    }

    public static void error(Object message, Throwable error) {
        StringWriter writer = new StringWriter();
        error.printStackTrace(new PrintWriter(writer));
        log(message + " - " + writer, LogLevel.ERROR);
    }

    public static void debug(Object message) {
        if (debugMode) {
            log(message, LogLevel.DEBUG);
        }
    }

    private enum LogLevel {
        INFO("Info"),
        WARN("Warn"),
        ERROR("Error"),
        DEBUG("Debug");

        private final String levelName;
        LogLevel(String levelName) {
            this.levelName = levelName;
        }
    }
}