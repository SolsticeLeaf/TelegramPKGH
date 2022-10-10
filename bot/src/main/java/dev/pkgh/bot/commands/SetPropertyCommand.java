package dev.pkgh.bot.commands;

import dev.pkgh.bot.data.UsersSqlHandler;
import dev.pkgh.bot.users.BotUser;
import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithPermission;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.type.UserPermission;
import lombok.val;

/**
 * Sets a property for user
 * @author winston
 */
@WithPermission(UserPermission.ADMINISTRATOR)
@NamedCommand("setproperty")
public final class SetPropertyCommand {

    @ExecutorMethod
    public static void execute(final ExecutionSession session) {
        if (session.getArguments().size() != 3) {
            session.sendMessage("Использование: `/setproperty <user> <prop> <value>`");
            return;
        }

        long user;
        try {
            user = Long.parseLong(session.getArguments().get(0));
        } catch (NumberFormatException e) {
            session.sendMessage("Использование: `/setproperty <user> <prop> <value>`");
            return;
        }

        String property = session.getArguments().get(1);
        int value;
        try {
            value = Integer.parseInt(session.getArguments().get(2));
        } catch (final NumberFormatException e) {
            session.sendMessage("Использование: `/setproperty <user> <prop> <value>`");
            return;
        }

        if (!UsersSqlHandler.IMP.isUserExists(user)) {
            session.sendMessage("Этот пользователь не зарегистрирован в БД бота.");
            return;
        }

        val targetUser = BotUser.getOrCreate(user);
        targetUser.setProperty(property, value);
        targetUser.saveAsync();

        session.sendMessage("Успешно установлена пропертия `" + property + "` = `" + value + "` для юзера `" + user + "`!");
    }

}
