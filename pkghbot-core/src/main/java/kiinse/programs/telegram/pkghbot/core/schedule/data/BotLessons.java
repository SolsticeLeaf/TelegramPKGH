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

package kiinse.programs.telegram.pkghbot.core.schedule.data;

import kiinse.programs.telegram.pkghbot.api.schedule.Lesson;
import kiinse.programs.telegram.pkghbot.api.schedule.Lessons;
import kiinse.programs.telegram.pkghbot.api.schedule.enums.LessonNumber;
import kiinse.programs.telegram.pkghbot.api.schedule.enums.Weekday;
import org.jetbrains.annotations.NotNull;
import org.json.JSONArray;

public class BotLessons implements Lessons {

    private final JSONArray data;
    private final Weekday weekday;
    private final String name;

    protected BotLessons(@NotNull JSONArray data, @NotNull Weekday weekday, @NotNull String name) {
        this.data = data;
        this.weekday = weekday;
        this.name = name;
    }

    @Override
    public boolean hasLesson(@NotNull LessonNumber number) {
        return getLesson(number).hasLesson();
    }

    @Override
    public @NotNull Weekday getDay() {
        return weekday;
    }

    @Override
    public @NotNull Lesson getLesson(@NotNull LessonNumber number) {
        return new BotLesson(data.getJSONArray(number.get()), weekday, number) {};
    }

    @Override
    public @NotNull String getName() {
        return name;
    }
}
