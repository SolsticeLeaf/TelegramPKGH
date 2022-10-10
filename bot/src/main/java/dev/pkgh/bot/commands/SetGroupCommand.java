package dev.pkgh.bot.commands;

import dev.pkgh.bot.sessions.SessionsManager;
import dev.pkgh.bot.sessions.impl.GroupAwaitSession;
import dev.pkgh.bot.util.KeyboardUtil;
import dev.pkgh.sdk.commands.NamedCommand;
import dev.pkgh.sdk.commands.WithAlias;
import dev.pkgh.sdk.commands.execution.ExecutionSession;
import dev.pkgh.sdk.commands.execution.ExecutorMethod;
import dev.pkgh.sdk.utils.MessageUtil;

/**
 * Setting up user's group
 * @author winston
 */
@WithAlias("группа")
@NamedCommand("group")
public final class SetGroupCommand {

    @ExecutorMethod
    public static void execute(final ExecutionSession session) {
        MessageUtil.sendMessage(String.valueOf(session.getTextChannel().getId()),
                "Введите название вашей группы ...\n" +
                "Пример: `СА-21-1` / `ТО-21-9`",
                KeyboardUtil.bakeCancelButton("cancel_session_group-await"));

        // Stating session
        SessionsManager.IMP.startSession(new GroupAwaitSession(session.getSender(), session.getTextChannel()));
    }

}
