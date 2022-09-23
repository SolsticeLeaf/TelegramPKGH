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
import kiinse.programs.telegram.pkghbot.api.utils.enums.Settings;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.core.schedule.ScheduleFactory;
import kiinse.programs.telegram.pkghbot.core.utils.PKGHUtils;
import kiinse.programs.telegram.pkghbot.core.utils.TimeUtils;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Класс команды /сегодня, /завтра и т.д.
 *
 * @author kiinse
 * @version 3.1.0
 * @since 3.1.0
 */
@Slf4j
@Command(
        name = "сегодня",
        aliases = {"завтра", "понедельник", "вторник", "среда", "четверг", "пятница", "суббота"}
)
public class GetScheduleCommand extends ICommand {

    @Override
    public void process(@NotNull Update rawUpdate, @NotNull String[] args, @NotNull User user) {
        if (getCommand().contains("завтра")) {
            BotUtils.sendMessage(
                    user,
                    PKGHUtils.getLessons(
                            ScheduleFactory.getGroup(user.getGroup()),
                            TimeUtils.toDay(TimeUtils.getNextWeekday()),
                            new Settings[]{Settings.NONE}));
        } else if (getCommand().contains("сегодня")) {
            BotUtils.sendMessage(
                    user,
                    PKGHUtils.getLessons(
                            ScheduleFactory.getGroup(user.getGroup()),
                            TimeUtils.toDay(TimeUtils.getWeekday()),
                            new Settings[]{Settings.NONE}));
        } else {
            BotUtils.sendMessage(
                    user,
                    PKGHUtils.getLessons(
                            ScheduleFactory.getGroup(user.getGroup()),
                            TimeUtils.toDay(getCommand()),
                            new Settings[]{Settings.NONE}));
        }
    }
}
