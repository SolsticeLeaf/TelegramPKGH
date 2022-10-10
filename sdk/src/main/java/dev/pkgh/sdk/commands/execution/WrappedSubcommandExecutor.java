package dev.pkgh.sdk.commands.execution;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NonNull;
import lombok.experimental.FieldDefaults;
import dev.pkgh.sdk.commands.CommandParams;

/**
 * @author winston
 */
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Getter
public final class WrappedSubcommandExecutor extends WrappedCommandExecutor {

    String[] names;
    String description;

    public WrappedSubcommandExecutor(final @NonNull String[] names,
                                     final @NonNull String description,
                                     final @NonNull CommandParams params) {
        super(params);

        this.description = description;
        this.names = names;
    }

    @Override
    public String toString() {
        return super.toString() + " " + names[0];
    }
}
