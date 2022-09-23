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

package kiinse.programs.telegram.pkghbot.core.utils;

import kiinse.programs.telegram.pkghbot.api.schedule.Group;
import kiinse.programs.telegram.pkghbot.api.schedule.Lesson;
import kiinse.programs.telegram.pkghbot.api.schedule.Lessons;
import kiinse.programs.telegram.pkghbot.api.schedule.enums.LessonNumber;
import kiinse.programs.telegram.pkghbot.api.schedule.enums.Weekday;
import kiinse.programs.telegram.pkghbot.api.utils.enums.MessageFormat;
import kiinse.programs.telegram.pkghbot.api.utils.enums.Settings;
import org.apache.commons.lang.RandomStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class PKGHUtils {

    private PKGHUtils() {}

    public static @NotNull String getLessons(@Nullable Group group, @NotNull Weekday weekday, @NotNull Settings[] settings) {
        if (group != null) {
            var msg = Arrays.asList(settings).contains(Settings.AUTO_MAILING) ? new StringBuilder(
                    "▒▒ Вечерняя рассылка ▒▒\n" + "#Расписание " + group.getName()) : new StringBuilder("#Расписание " + group.getName());
            if (weekday.equals(Weekday.WEEK)) {
                msg
                        .append(msgBuilder(group.getLessons(Weekday.MONDAY), MessageFormat.WEEKLY))
                        .append(msgBuilder(group.getLessons(Weekday.TUESDAY), MessageFormat.WEEKLY))
                        .append(msgBuilder(group.getLessons(Weekday.WEDNESDAY), MessageFormat.WEEKLY))
                        .append(msgBuilder(group.getLessons(Weekday.THURSDAY), MessageFormat.WEEKLY))
                        .append(msgBuilder(group.getLessons(Weekday.FRIDAY), MessageFormat.WEEKLY))
                        .append(msgBuilder(group.getLessons(Weekday.SATURDAY), MessageFormat.WEEKLY));
            } else {
                if (!weekday.equals(Weekday.SUNDAY)) {
                    msg.append(msgBuilder(group.getLessons(weekday), MessageFormat.DAILY));
                } else {
                    msg.append("\nВообще-то в этот день воскресенье :3");
                }
            }
            return msg.toString();
        } else {
            return ("❌ Произошла ошибка.");
        }
    }

    public static @NotNull StringBuilder msgBuilder(@NotNull Lessons groupLessons, @NotNull MessageFormat format) {
        var msg = new StringBuilder("\n<b>▬▬▬▬ " + groupLessons.getDay().getRussian() + " ▬▬▬▬</b>");
        var lessons = new StringBuilder();
        lessons
                .append(lesson(groupLessons.getLesson(LessonNumber.FIRST), format))
                .append(lesson(groupLessons.getLesson(LessonNumber.SECOND), format))
                .append(lesson(groupLessons.getLesson(LessonNumber.THIRD), format))
                .append(lesson(groupLessons.getLesson(LessonNumber.FOURTH), format))
                .append(lesson(groupLessons.getLesson(LessonNumber.FIFTH), format))
                .append(lesson(groupLessons.getLesson(LessonNumber.SIXTH), format));
        if (lessons.toString().replace(" ", "").length() == 0) {
            return msg.append("\nВ этот день пар нет :3\n");
        }
        return msg.append(lessons);
    }

    public static @NotNull StringBuilder lesson(@NotNull Lesson lesson, @NotNull MessageFormat format) {
        var msg = new StringBuilder();
        var isNum = !TimeUtils.isDenSubject();
        if (lesson.hasLesson()) {
            msg.append("\n<u>→ Пара №").append(lesson.getNumber().get() + 1).append(" | ").append(lesson.getTime()).append(" ←</u>\n");
            if (format.equals(MessageFormat.WEEKLY)) {
                if (lesson.isDuplicated()) {
                    msg.append(lesson.getNum().getFormattedLesson());
                } else {
                    if (lesson.hasNum()) {
                        var numLesson = lesson.getNum();
                        if (isNum) {
                            msg.append(numLesson.getActiveFormattedLesson());
                        } else {
                            msg.append(numLesson.getFormattedLesson());
                        }
                        if (lesson.hasDen()) {
                            msg.append("\n---------------\n");
                        }
                    }
                    if (lesson.hasDen()) {
                        var denLesson = lesson.getDen();
                        if (!isNum) {
                            msg.append(denLesson.getActiveFormattedLesson());
                        } else {
                            msg.append(denLesson.getFormattedLesson());
                        }
                    }
                }
            } else {
                if (lesson.isDuplicated()) {
                    msg.append(lesson.getNum().getFormattedLesson());
                } else {
                    var les = isNum ? lesson.getNum().getSubject() : lesson.getDen().getSubject();
                    var formatted = isNum ? lesson.getNum().getFormattedLesson() : lesson.getDen().getFormattedLesson();
                    var noLesson = isNum ? "Занятий по числителю нет" : "Занятий по знаменателю нет";
                    msg.append((les == null || les.isBlank()) ? noLesson : formatted);
                }
            }
            msg.append("\n");
        }
        return msg;
    }

    public static @NotNull String getRandomASCII(int length) {
        return RandomStringUtils.randomAscii(length);
    }
}
