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

package kiinse.programs.telegram.pkghbot.bot.schedulers;

import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import kiinse.programs.telegram.pkghbot.api.utils.enums.Settings;
import kiinse.programs.telegram.pkghbot.bot.utilities.BotUtils;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import kiinse.programs.telegram.pkghbot.core.schedule.ScheduleFactory;
import kiinse.programs.telegram.pkghbot.core.utils.PKGHUtils;
import kiinse.programs.telegram.pkghbot.core.utils.TimeUtils;

import java.text.SimpleDateFormat;
import java.util.TimerTask;

public class EveningSchedule extends TimerTask {

    @Override
    public void run() {
        var now = TimeUtils.getDate();
        var sdf = new SimpleDateFormat("HH:mm");
        if (sdf.format(now).equals("20:00")) new Thread(this::sendMailing).start();
    }

    public void sendMailing() {
        for (var user : UserQuery.getActiveUsers()) {
            if (user.isMailing() && user.getStatus() == UserStatus.ACTIVE && !TimeUtils.getNextWeekday().equals("Воскресенье"))
                BotUtils.sendAutoMailing(
                        user,
                        PKGHUtils.getLessons(
                                ScheduleFactory.getGroup(user.getGroup()),
                                TimeUtils.toDay(TimeUtils.getNextWeekday()),
                                new Settings[]{Settings.AUTO_MAILING}));
        }
    }
}
