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

package kiinse.programs.telegram.pkghbot.bot.commands.usercommands;

import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.core.data.Config;
import kiinse.programs.telegram.pkghbot.core.data.Messages;
import kiinse.programs.telegram.pkghbot.core.data.Text;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import kiinse.programs.telegram.pkghbot.core.mongo.data.BotUser;
import kiinse.programs.telegram.pkghbot.core.schedule.ScheduleFactory;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import xyz.winston.parser.core.ScheduleRenderer;

/**
 * Класс команд, которые не добавить в менеджере команд
 *
 * @author kiinse
 * @version 3.1.1
 * @since 3.1.0
 */
@Slf4j
public class OtherCommands {

    public void firstMsg(long chat, @NotNull Update rawUpdate) {
        var from = rawUpdate.getMessage().getFrom();
        BotUtils.sendMessage(
                chat,
                String.format(Text.getText(BotUtils.isGroupMessage(rawUpdate), Text.TextName.FIRSTSTART),
                              from.getFirstName(),
                              Config.getProperty("admins").replace("|", "или")));
        UserQuery.createUser(
                new BotUser()
                        .setName(from.getUserName() != null ? from.getUserName() : from.getFirstName())
                        .setChatId(chat)
                        .setGroupChat(BotUtils.isGroupMessage(rawUpdate)));
        BotUtils.sendMessage(
                chat,
                Text.getText(BotUtils.isGroupMessage(rawUpdate), Text.TextName.EDITGROUP));
    }

    public void setGroup(long chat, @NotNull Update rawUpdate) {
        var text = rawUpdate.getMessage().getText();
        var group = text.substring(0, text.length() - 1).toUpperCase() + text.substring(text.length() - 1).toLowerCase();
        var user = UserQuery.getUser(chat);

        //TODO: ScheduleFactory.hasGroup(group)
        if (user != null && checkGroup(group)) {
            user.setGroup(group).setStatus(UserStatus.ACTIVE).upload();
            BotUtils.sendMessage(
                    chat,
                    Messages.getText(Messages.Message.GROUPSAVEDMSG).replace("GROUP", group));
            new StartCommand().process(rawUpdate, new String[]{}, user);
        } else {
            BotUtils.sendMessage(
                    chat,
                    Messages.getText(Messages.Message.GROUPNOTFOUNDMSG).replace("GROUP", group));
        }
    }

    private boolean checkGroup(String group) {
        try {
            ScheduleRenderer.ofGroup(group).bakeImage();
        } catch (Exception ignored) {
            return false;
        }
        return true;
    }
}
