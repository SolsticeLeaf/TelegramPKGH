package dev.pkgh.sdk.commands.exception;

import lombok.AccessLevel;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import dev.pkgh.sdk.commands.execution.ExecutionSession;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public final class CommandMessageException extends CommandException {

    String[] messages;

    public CommandMessageException(String... messages) {
        this.messages = messages;
    }

    public void printMessages(final @NonNull ExecutionSession session) {
        for (var message : messages) {
            session.sendMessage(message);
        }
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return this;
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }

}
