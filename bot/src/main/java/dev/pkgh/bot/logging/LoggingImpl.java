package dev.pkgh.bot.logging;

import dev.pkgh.sdk.logging.Logging;
import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.io.IoBuilder;
import xyz.winston.impl.Impl;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Impl(Logging.class)
public final class LoggingImpl extends Logging {

    Logger logger;

    public LoggingImpl() {
        this.logger = LogManager.getLogger("PKGHBot");

        System.setOut(IoBuilder.forLogger(logger).setLevel(Level.INFO).buildPrintStream());
        System.setErr(IoBuilder.forLogger(logger).setLevel(Level.WARN).buildPrintStream());
    }

    @Override
    public void debug(final @NonNull String message) {
        this.logger.debug(message);
    }

    @Override
    public void info(final @NonNull String message) {
        this.logger.info(message);
    }

    @Override
    public void warn(final @NonNull String message) {
        this.logger.warn(message);
    }

    @Override
    public void error(final @NonNull String message) {
        this.logger.error(message);
    }

    @Override
    public void stacktrace(final @NonNull Throwable t,
                           final @NonNull String message) {
        this.logger.fatal(message, t);
    }
}
