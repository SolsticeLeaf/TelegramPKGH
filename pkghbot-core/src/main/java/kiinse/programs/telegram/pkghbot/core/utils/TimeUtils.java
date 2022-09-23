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

import kiinse.programs.telegram.pkghbot.api.schedule.enums.Weekday;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

public class TimeUtils {

    private TimeUtils() {}

    public static @NotNull Date getDate() {
        var nowUtc = Instant.now();
        var moscow = ZoneId.of("Europe/Moscow");
        return Date.from(ZonedDateTime.ofInstant(nowUtc, moscow).toInstant());
    }

    public static @NotNull Calendar getCalendar() {
        var cal = Calendar.getInstance();
        cal.setTimeZone(TimeZone.getTimeZone("Europe/Moscow"));
        return cal;
    }

    public static @NotNull String getWeekday() {
        var now = getDate();
        var day = new SimpleDateFormat("EEEEE").format(now);
        if (isCyrillic(day)) {
            return day.substring(0, 1).toUpperCase() + day.substring(1);
        } else {
            return getRussian(day);
        }
    }

    public static @NotNull Weekday toDay(@NotNull String day) {
        return switch (day.toLowerCase()) {
            case "понедельник" -> Weekday.MONDAY;
            case "вторник" -> Weekday.TUESDAY;
            case "среда" -> Weekday.WEDNESDAY;
            case "четверг" -> Weekday.THURSDAY;
            case "пятница" -> Weekday.FRIDAY;
            case "суббота" -> Weekday.SATURDAY;
            case "воскресенье" -> Weekday.SUNDAY;
            default -> Weekday.WEEK;
        };
    }

    public static @NotNull String getNextWeekday() {
        var now = getDate();
        var cal = getCalendar();
        cal.setTime(now);
        cal.add(Calendar.DATE, 1);
        var date = cal.getTime();
        var day = new SimpleDateFormat("EEEEE").format(date);
        if (isCyrillic(day)) {
            return day.substring(0, 1).toUpperCase() + day.substring(1);
        } else {
            return getRussian(day);
        }
    }

    public static @NotNull String getRussian(@NotNull String day) {
        return switch (day.toLowerCase()) {
            case "monday" -> "Понедельник";
            case "tuesday" -> "Вторник";
            case "wednesday" -> "Среда";
            case "thursday" -> "Четверг";
            case "friday" -> "Пятница";
            case "saturday" -> "Суббота";
            default -> "Воскресенье";
        };
    }

    public static boolean isCyrillic(@NotNull String string) {
        for (char thisChar : string.toCharArray()) {
            if (Character.UnicodeBlock.of(thisChar) == Character.UnicodeBlock.CYRILLIC) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDenSubject() {
        return getWeekday().equals("Воскресенье") != ((getCalendar().get(Calendar.WEEK_OF_MONTH) % 2) == 0);
    }
}
