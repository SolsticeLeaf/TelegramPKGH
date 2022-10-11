package dev.pkgh.sdk.commands;

import dev.pkgh.sdk.type.IBotUser;
import dev.pkgh.sdk.type.UserPermission;
import lombok.NonNull;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.List;

/**
 * Command interface
 *
 * @author winston
 */
public interface ICommand {

    /**
     * Get command primary name
     * @return  command primary name
     */
    @NotNull String getName();

    /**
     * Get command aliases
     * @return  aliases of the command
     */
    String @NotNull [] getAliases();

    /**
     * Description of the command (required for discord command completion)
     * @return command description
     */
    String getDescription();

    /**
     * @return  Minimal permission, that allows command execution
     */
    UserPermission getPermission();

    /**
     * Processes command execution
     *
     * @param sender    discord member instance of sender
     * @param args      arguments of command
     */
    void execute(final @NonNull IBotUser user,
                 final @NotNull Chat channel,
                 final @NotNull User sender,
                 final @NotNull List<String> args,
                 final @NotNull Update update);

}