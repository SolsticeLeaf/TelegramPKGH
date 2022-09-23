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

package kiinse.programs.telegram.pkghbot.bot.callbackdata.messages;

import kiinse.programs.telegram.pkghbot.api.callback.CallBackData;
import kiinse.programs.telegram.pkghbot.api.callback.ICallBackData;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.messages.enums.MessageStatus;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.core.data.Messages;
import kiinse.programs.telegram.pkghbot.core.mongo.MessageQuery;
import kiinse.programs.telegram.pkghbot.core.utils.TextUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Objects;
import java.util.UUID;

/**
 * Класс CallBackData команды /send
 *
 * @author kiinse
 * @version 3.1.0
 * @since 3.1.0
 */
@Slf4j
@CallBackData(
        name = "send_private_message",
        isAdmin = true
)
public class SendPrivateMessageCallBack extends ICallBackData {

    @Override
    public void process(@NotNull Update rawUpdate, @NotNull String[] args, @NotNull User user, int messageId) {
        if (args.length < 1) {
            return;
        }

        var message = MessageQuery.getMessage(UUID.fromString(args[0]));

        if (message == null) {
            BotUtils.editMessageText(
                    user,
                    messageId,
                    Messages.getText(Messages.Message.MESSAGENOTFOUND).replace("ID", args[0]));
            return;
        }

        if (message.getStatus() == MessageStatus.SENT) {
            BotUtils.editMessageText(
                    user,
                    messageId,
                    Messages.getText(Messages.Message.MESSAGEALREADYSENDED).replace("ID", args[0]));
            return;
        }
        message.setStatus(MessageStatus.SENT).upload();

        var chat = Objects.requireNonNull(message.getUser());

        BotUtils.sendMessage(
                chat,
                message.getText());

        BotUtils.editMessageText(
                user,
                messageId,
                TextUtils.emojis.SUCCESS.getValue() + "Сообщение отправлено в чат " + chat.getChatId() + " (" + chat.getName() +")!\n Текст: " + message.getText());
    }
}
