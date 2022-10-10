package dev.pkgh.sdk.commands.exception;

import lombok.NonNull;

/**
 * Base command exception class
 * @author winston
 */
public class CommandException extends Exception {

    public CommandException() {
        super();
    }

    public CommandException(final @NonNull String message) {
        super(message);
    }

    public CommandException(final @NonNull String message,
                            final @NonNull Throwable cause) {
        super(message, cause);
    }
}
