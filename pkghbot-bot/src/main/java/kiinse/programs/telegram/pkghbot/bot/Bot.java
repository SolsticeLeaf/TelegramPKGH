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

package kiinse.programs.telegram.pkghbot.bot;

import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.CallBackDataManager;
import kiinse.programs.telegram.pkghbot.bot.commands.CommandsManager;
import kiinse.programs.telegram.pkghbot.bot.commands.usercommands.OtherCommands;
import kiinse.programs.telegram.pkghbot.bot.data.BotSettings;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

public class Bot extends TelegramLongPollingBot {

    private static Bot instance;

    private final String botToken;

    private final String botName;

    private final OtherCommands otherCommands;

    private final CommandsManager commandsManager;

    private final CallBackDataManager callBackDataManager;

    public Bot(BotSettings botSettings) {
        Bot.instance = this;
        this.otherCommands = botSettings.getOtherCommands();
        this.commandsManager = botSettings.getCommandsManager();
        this.callBackDataManager = botSettings.getCallBackDataManager();
        this.botToken = botSettings.getBotToken();
        this.botName = botSettings.getBotName();
    }

    public static Bot getInstance() {
        return instance;
    }

    @Override
    public void onUpdateReceived(Update rawUpdate) {
        new Thread(() -> {
            if (rawUpdate.hasChannelPost() || rawUpdate.hasEditedChannelPost()) {
                return;
            }
            var chat = rawUpdate.hasCallbackQuery() ? rawUpdate.getCallbackQuery().getMessage().getChatId() : rawUpdate.getMessage().getChatId();
            var user = UserQuery.getUser(chat);
            if (rawUpdate.hasCallbackQuery()) {
                new Thread(() -> callBackExecute(rawUpdate, user)).start();
            } else {
                new Thread(() -> commandExecute(rawUpdate, user, chat)).start();
                new Thread(() -> updateUserName(rawUpdate, user)).start();
            }
        }).start();
    }

    private void callBackExecute(@NotNull Update rawUpdate, @Nullable User user) {
        if (user != null && user.getStatus() == UserStatus.ACTIVE) {
            callBackDataManager.process(rawUpdate);
        }
    }

    private void commandExecute(@NotNull Update rawUpdate, @Nullable User user, @NotNull Long chat) {
        if (user == null) {
            otherCommands.firstMsg(chat, rawUpdate);
        } else if (user.getStatus() != UserStatus.ACTIVE) {
            otherCommands.setGroup(chat, rawUpdate);
        } else {
            commandsManager.process(rawUpdate);
        }
    }

    private void updateUserName(@NotNull Update rawUpdate, @Nullable User user) {
        if (user != null) {
            var username = rawUpdate.hasCallbackQuery() ? rawUpdate.getCallbackQuery().getFrom() : rawUpdate.getMessage().getFrom();
            var finalUserName = username.getUserName() != null ? username.getUserName() : username.getFirstName();
            if (!user.getName().equalsIgnoreCase(finalUserName)) {
                user.setName(finalUserName);
                UserQuery.updateUser(user);
            }
        }
    }

    @Override
    public @NotNull String getBotUsername() {
        return botName;
    }

    @Override
    public @NotNull String getBotToken() {
        return botToken;
    }
}


