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

package kiinse.programs.telegram.pkghbot.bot.callbackdata;

import kiinse.programs.telegram.pkghbot.api.callback.CallBackData;
import kiinse.programs.telegram.pkghbot.api.callback.ICallBackData;
import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.adminsettings.AdminCloseCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.adminsettings.AdminNotificationsCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.adminsettings.AdminOpenCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.messages.CancelAllCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.messages.CancelPrivateMessageCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.messages.SendAllCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.messages.SendPrivateMessageCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.posts.AcceptPostCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.posts.RejectPostCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.schedule.GetScheduleCallBack;
import kiinse.programs.telegram.pkghbot.bot.callbackdata.usersettings.*;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.core.data.Messages;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import lombok.Getter;
import org.glassfish.grizzly.utils.ArrayUtils;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Менеджер CallBackData
 *
 * @author Tomskiy
 * Изменён kiinse
 * @version 3.1.1
 * @since 3.1.0
 */
public class CallBackDataManager {

    @Getter
    private final Set<ICallBackData> commands;

    public CallBackDataManager() {
        commands = new HashSet<>();
        collect();
    }

    /** Регистрация классов CallBackData бота */
    protected void collect() {
        registerCallBackData(new AdminCloseCallBack());
        registerCallBackData(new AdminOpenCallBack());
        registerCallBackData(new AdminNotificationsCallBack());
        registerCallBackData(new CancelAllCallBack());
        registerCallBackData(new CancelPrivateMessageCallBack());
        registerCallBackData(new SendAllCallBack());
        registerCallBackData(new SendPrivateMessageCallBack());
        registerCallBackData(new AcceptPostCallBack());
        registerCallBackData(new RejectPostCallBack());
        registerCallBackData(new GetScheduleCallBack());
        registerCallBackData(new AddGroupCallBack());
        registerCallBackData(new CloseSettingsCallBack());
        registerCallBackData(new ChangeGroupCallBack());
        registerCallBackData(new LessonMailingCallBack());
        registerCallBackData(new MailingCallBack());
        registerCallBackData(new OpenSettingsCallBack());
    }

    /**
     * Регистрация CallBackData
     *
     * @param callBackData Поступающий класс команды {@link ICallBackData}
     */
    protected void registerCallBackData(@NotNull ICallBackData callBackData) {
        if (commands.contains(callBackData))
            throw new IllegalStateException("CallBackData " + callBackData.getClass().getName() + " is already registered! ");
        if (!callBackData.getClass().isAnnotationPresent(CallBackData.class))
            throw new IllegalStateException("CallBackData " + callBackData.getClass().getName() + " does not contains required annotation!");
        commands.add(callBackData);
    }

    /**
     * Выполнение поступающей CallBackData в rawUpdate
     *
     * @param rawUpdate Переменная, содержащая в себе данные, поступившие от пользователя {@link Update}
     */
    public void process(@NotNull Update rawUpdate) {
        String[] components = rawUpdate.getCallbackQuery().getData().split(" ");
        String[] args = Arrays.copyOfRange(components, 1, components.length);
        ICallBackData callBack = getCallBackData(components[0]);
        if (callBack == null) return;

        var chat = rawUpdate.getCallbackQuery().getMessage().getChatId();
        var user = UserQuery.getUser(chat);
        if (user == null) {
            LoggerUtils.getLogger().warn("User '{}' not found!", chat);
            return;
        }

        if (canExecute(getAnnotation(callBack), user)) {
            callBack.setCallBack(components[0]).process(rawUpdate,
                                                        args,
                                                        user,
                                                        rawUpdate.getCallbackQuery().getMessage().getMessageId());
        } else {
            BotUtils.sendMessage(
                    user,
                    Messages.getText(Messages.Message.NOPERMISSIONSMSG));
        }
    }

    public boolean canExecute(@NotNull CallBackData data, @NotNull User user) {
        if (data.isAdmin()) return user.isAdmin();
        return true;
    }

    /**
     * Метод получения CallBackData из зарегистрированных CallBackData
     *
     * @param name Имя CallBackData
     * @return Класс, содержащий указанную CallBackData
     */
    public ICallBackData getCallBackData(@NotNull String name) {
        for (var callBackData : commands) {
            var args = callBackData.getClass().getAnnotation(CallBackData.class).aliases();
            args = ArrayUtils.addUnique(args, callBackData.getClass().getAnnotation(CallBackData.class).name());
            if (Arrays.stream(args).noneMatch(a -> a.equalsIgnoreCase(name))) continue;
            return callBackData;
        }
        return null;
    }

    /**
     * Получение аннотаций из класса с CallBackData
     *
     * @param iCallBackData Класс CallBackData {@link ICallBackData}
     * @return Аннотации
     */
    public CallBackData getAnnotation(@NotNull ICallBackData iCallBackData) {
        return iCallBackData.getClass().getAnnotation(CallBackData.class);
    }
}
