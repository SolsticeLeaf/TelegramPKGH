package dev.pkgh.sdk.logging;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import xyz.winston.impl.Impl;
import xyz.winston.impl.ImplPriority;

import java.lang.management.ManagementFactory;

/**
 * @author WhileInside
 */
@Impl(value = Logging.class, priority = ImplPriority.HIGHEST)
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class SoutLogging extends Logging {

    boolean debug;

    public SoutLogging() {
        boolean debug = false;

        for (String arg : ManagementFactory.getRuntimeMXBean().getInputArguments()) {
            if (arg.contains("jdwp=")) {
                debug = true;
                break;
            }
        }

        this.debug = debug;
    }

    @Override
    public void debug(final @NonNull String message) {
        if (debug) {
            System.out.println(message);
        }
    }

    @Override
    public void info(final @NonNull String message) {
        System.out.println(message);
    }

    @Override
    public void warn(final @NonNull String message) {
        System.out.println(message);
    }

    @Override
    public void error(final @NonNull String message) {
        System.err.println(message);
    }

    @Override
    public void stacktrace(final @NonNull Throwable t, final @NonNull String message) {
        System.err.println(message);

        t.printStackTrace();
    }
}
