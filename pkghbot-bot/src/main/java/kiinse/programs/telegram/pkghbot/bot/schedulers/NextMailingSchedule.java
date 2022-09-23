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

import kiinse.programs.telegram.pkghbot.api.schedule.enums.LessonNumber;
import kiinse.programs.telegram.pkghbot.api.utils.enums.MessageFormat;
import kiinse.programs.telegram.pkghbot.core.schedule.ScheduleFactory;
import kiinse.programs.telegram.pkghbot.core.utils.PKGHUtils;
import kiinse.programs.telegram.pkghbot.core.utils.TimeUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.TimerTask;

public class NextMailingSchedule extends TimerTask {

    @Override
    public void run() {
//        var now = TimeUtils.getDate();
//        var sdf = new SimpleDateFormat("HH:mm");
//        if (!TimeUtils.getWeekday().equals("Суббота")) {
//            sendMailing(sdf.format(now), LessonsTime.getNormalMap());
//        } else {
//            sendMailing(sdf.format(now), LessonsTime.getWeekendMap());
//        }
    }

//    public void sendMailing(String time, Map<String, Integer> day) {
//        if (day.containsKey(time)) {
//            new Thread(() -> {
//                for (int i = 1; i <= UserQuery.countChats(); i++) {
//                    var chat = UserQuery.getChat(i);
//                    if (BotUtils.checkMailing(BotUtils.Mailing.NEXTLESSONMAILING,
//                                              chat) && !TimeUtils.getWeekday().equals("Воскресенье")) {
//                        var lesson = getLessons(UserQuery.getGroup(chat), TimeUtils.getWeekday(), day.get(time));
//                        if (lesson != null) {
//                            BotUtils.sendMessage(
//                                    chat,
//                                    lesson,
//                                    null,
//                                    null
//                                                );
//                        }
//                    }
//                }
//            }).start();
//        }
//    }

    public String getLessons(String group, String day, int lesson) {
        var msg = new StringBuilder("<b>▬▬▬ Следующая пара ▬▬▬</b>");
        var data = Objects.requireNonNull(ScheduleFactory.getGroup(group)).getLessons(TimeUtils.toDay(day)).getLesson(
                toTime(lesson));
        if (data.hasLesson()) {
            msg.append("\n").append(PKGHUtils.lesson(data, MessageFormat.DAILY));
            return msg.toString().replace(" ", "").equals("<b>▬▬▬Следующаяпара▬▬▬</b>") ? null : msg.toString();
        }
        return null;
    }

    private @NotNull LessonNumber toTime(int lesson) {
        return switch (lesson) {
            case 1 -> LessonNumber.FIRST;
            case 2 -> LessonNumber.SECOND;
            case 3 -> LessonNumber.THIRD;
            case 4 -> LessonNumber.FOURTH;
            case 5 -> LessonNumber.FIFTH;
            default -> LessonNumber.SIXTH;
        };
    }
}
