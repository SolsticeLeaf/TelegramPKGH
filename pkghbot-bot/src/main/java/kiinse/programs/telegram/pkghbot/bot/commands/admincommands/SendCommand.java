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

package kiinse.programs.telegram.pkghbot.bot.commands.admincommands;

import kiinse.programs.telegram.pkghbot.api.commands.Command;
import kiinse.programs.telegram.pkghbot.api.commands.ICommand;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.messages.enums.MessageType;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.bot.utilities.KeyboardsFactory;
import kiinse.programs.telegram.pkghbot.core.data.Messages;
import kiinse.programs.telegram.pkghbot.core.mongo.MessageQuery;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import kiinse.programs.telegram.pkghbot.core.mongo.data.BotMessage;
import kiinse.programs.telegram.pkghbot.core.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * Класс команды /send
 *
 * @author kiinse
 * @version 3.1.0
 * @since 3.1.0
 */
@Slf4j
@Command(
        name = "send",
        isAdmin = true
)
public class SendCommand extends ICommand {

    @Override
    public void process(@NotNull Update rawUpdate, @NotNull String[] args, @NotNull User user) {
        if (args.length < 2) {
            BotUtils.sendMessage(
                    user,
                    Messages.getText(Messages.Message.EMPTYMESSAGEMSG));
            return;
        }
        var chat = getChatId(rawUpdate.getMessage().getText());
        var chatUser = UserQuery.getUser(Long.parseLong(chat));

        if (chatUser == null) {
            BotUtils.sendMessage(
                    user,
                    Messages.getText(Messages.Message.USERNOTFOUNDMSG).replace("USER", chat));
            return;
        }

        var message = MessageQuery.createMessage(new BotMessage()
                                                         .setUser(chatUser)
                                                         .setText(rawUpdate.getMessage().getText().split("\\d+")[1].substring(1))
                                                         .setType(MessageType.PRIVATE));

        BotUtils.sendMessage(
                user,
                "<b>Сообщение:</b>\n\n" + message.getText() + "\n\nОтправить его в чат " + chatUser.getChatId() + " | @" + chatUser.getName() + "?",
                KeyboardsFactory.inlineConstructor("Отправить" + TextUtils.emojis.SUCCESS.getValue(),
                                                   "send_private_message " + message.getId(),
                                                   "Отменить" + TextUtils.emojis.ERROR.getValue(),
                                                   "cancel_private_message " + message.getId()));
    }

    private @NotNull String getChatId(@NotNull String text) {
        var arr = new ArrayList<String>();
        var matcher = Pattern.compile("\\d+").matcher(text);
        while (matcher.find()) {
            arr.add(matcher.group());
        }
        return arr.get(0);
    }
}
