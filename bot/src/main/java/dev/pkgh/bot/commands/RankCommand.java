package dev.pkgh.bot.commands;

import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithPermission;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.type.UserPermission;

/**
 * Command, required for changing user's groups
 * @author winston
 */
@WithPermission(UserPermission.ADMINISTRATOR)
@NamedCommand("group")
public final class RankCommand {

    @ExecutorMethod
    public static void execute(ExecutionSession session) {
        if (session.getArguments().size() != 2) {
            session.sendMessage("Использование: /group @user [USER/ADMIN]");
            return;
        }

        // Ik its shit xd
        var userStr = session.getUpdate().getMessage().getEntities().get(0);
        var groupStr = session.getArguments().get(1);

        session.sendMessage(userStr.toString());
    }

}
