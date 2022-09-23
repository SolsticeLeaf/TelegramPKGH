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

import kiinse.programs.telegram.pkghbot.api.commands.Command;
import kiinse.programs.telegram.pkghbot.api.commands.ICommand;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostSettings;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.bot.utilities.KeyboardsFactory;
import kiinse.programs.telegram.pkghbot.core.data.Messages;
import kiinse.programs.telegram.pkghbot.core.mongo.PostQuery;
import kiinse.programs.telegram.pkghbot.core.mongo.data.BotPost;
import kiinse.programs.telegram.pkghbot.core.utils.TextUtils;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Command(
        name = "post",
        aliases = {"apost"},
        canFromGroup = false
)
public class SendPostCommand extends ICommand {

    @Override
    public void process(@NotNull Update rawUpdate, @NotNull String[] args, @NotNull User user) {
        var message = rawUpdate.getMessage();
        var text = (message.getCaption() == null ? message.getText() : message.getCaption()).replace("/", "");

        if (!text.isBlank() && !text.equals("post") && !text.equals("apost")) {
            var post = PostQuery.createPost(new BotPost()
                                                    .setUser(user)
                                                    .setText(text.replace(getCommand() + " ", ""))
                                                    .setSettings(getCommand().equalsIgnoreCase("post") ?
                                                                         PostSettings.NORMAL : PostSettings.ANONYMOUS));
            try {
                BotUtils.sendPostPreview(user, post);
            } catch (TelegramApiException e) {
                BotUtils.sendMessage(
                        user,
                        Messages.getText(Messages.Message.ERRORMSG));
            }
            BotUtils.sendMessage(
                    user,
                    "Предложить пост?",
                    KeyboardsFactory.inlineConstructor("Да" + TextUtils.emojis.SUCCESS.getValue(),
                                                       "post-add " + post.getId(),
                                                       "Нет" + TextUtils.emojis.ERROR.getValue(),
                                                       "post-cancel " + post.getId()));
        } else {
            BotUtils.sendMessage(
                    user,
                    Messages.getText(Messages.Message.EMPTYMESSAGEMSG));
        }
    }

}