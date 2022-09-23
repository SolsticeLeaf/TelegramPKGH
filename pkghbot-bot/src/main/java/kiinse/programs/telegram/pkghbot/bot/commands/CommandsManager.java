/*
 * MIT License
 *
 * Copyright (c) 2022 ⭕️⃤ kiinse
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package kiinse.programs.telegram.pkghbot.bot.commands;

import kiinse.programs.telegram.pkghbot.api.commands.Command;
import kiinse.programs.telegram.pkghbot.api.commands.ICommand;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import kiinse.programs.telegram.pkghbot.bot.Bot;
import kiinse.programs.telegram.pkghbot.bot.commands.admincommands.*;
import kiinse.programs.telegram.pkghbot.bot.commands.usercommands.*;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.core.data.Messages;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import org.glassfish.grizzly.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер команд
 *
 * @author Tomskiy
 * Изменён kiinse
 * @version 3.1.1
 * @since 3.1.0
 */
public class CommandsManager {

    private final Set<ICommand> commands;

    public CommandsManager() {
        commands = new HashSet<>();
        collect();
    }

    /** Регистрация классов команд бота */
    protected void collect() {
        registerCommand(new StartCommand());
        registerCommand(new ScheduleCommand());
        registerCommand(new SettingsCommand());
        registerCommand(new GetScheduleCommand());
        registerCommand(new GetScheduleListCommand());
        registerCommand(new CommandsListCommand());
        registerCommand(new UsersCommand());
        registerCommand(new SendCommand());
        registerCommand(new AllCommand());
        registerCommand(new AdminSettingsCommand());
        registerCommand(new AdminsCommand());
        registerCommand(new EveningScheduleCommand());
        registerCommand(new SendPostCommand());
        registerCommand(new SendPostHelpCommand());
    }

    /**
     * Регистрация команды
     *
     * @param command Поступающий класс команды {@link ICommand}
     */
    protected void registerCommand(@NotNull ICommand command) {
        if (commands.contains(command)) {
            throw new IllegalStateException("Command " + command.getClass().getName() + " is already registered! ");
        }
        if (!command.getClass().isAnnotationPresent(Command.class)) {
            throw new IllegalStateException("Command " + command.getClass().getName() + " does not contains required annotation!");
        }
        commands.add(command);
    }

    /**
     * Выполнение поступающей команды в rawUpdate
     *
     * @param rawUpdate Переменная, содержащая в себе данные, поступившие от пользователя {@link Update}
     */
    public void process(@NotNull Update rawUpdate) {
        var msg = rawUpdate.getMessage().getCaption() == null ? rawUpdate.getMessage().getText() : rawUpdate.getMessage().getCaption();

        String text = msg.replace("@" + Bot.getInstance().getBotUsername(), "");
        String[] components = text.replaceFirst("/", "").split(" ");
        String[] args = Arrays.copyOfRange(components, 1, components.length);
        ICommand command = getCommand(components[0]);
        if (command == null) {
            return;
        }

        var chat = rawUpdate.getMessage().getChatId();
        var user = UserQuery.getUser(chat);
        if (user == null) {
            LoggerUtils.getLogger().warn("User '{}' not found!", chat);
            return;
        }

        if (canExecute(getAnnotation(command), user)) {
            command.setCommand(components[0]).process(rawUpdate,
                                                      args,
                                                      user);
        } else {
            BotUtils.sendMessage(
                    user,
                    Messages.getText(Messages.Message.NOPERMISSIONSMSG));
        }
    }

    public boolean canExecute(@NotNull Command data, @NotNull User user) {
        if (data.isAdmin()) {
            return user.isAdmin();
        }
        if (!data.canFromGroup()) {
            return !user.isGroup();
        }
        return true;
    }

    /**
     * Метод получения команды из зарегистрированных команд
     *
     * @param name Имя команды
     * @return Класс, содержащий указанную команду
     */
    public ICommand getCommand(@NotNull String name) {
        for (var command : commands) {
            var args = command.getClass().getAnnotation(Command.class).aliases();
            args = ArrayUtils.addUnique(args, command.getClass().getAnnotation(Command.class).name());
            if (Arrays.stream(args).noneMatch(a -> a.equalsIgnoreCase(name))) continue;
            return command;
        }
        return null;
    }

    /**
     * Получение аннотаций из класса с командой
     *
     * @param iCommand Класс команды {@link ICommand}
     * @return Аннотации
     */
    public Command getAnnotation(@NotNull ICommand iCommand) {
        return iCommand.getClass().getAnnotation(Command.class);
    }
}
