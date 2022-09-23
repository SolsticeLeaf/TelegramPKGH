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

package kiinse.programs.telegram.pkghbot.api.schedule;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

public abstract class NumLesson {

    private final JSONObject data;

    protected NumLesson(@NotNull JSONObject data) {
        this.data = data;
    }

    public @Nullable String getSubject() {
        if (data.has("name")) {
            var name = data.getString("name");
            if (!name.isBlank()) {
                return name.replace("\n", " ").replace("||", " // ");
            }
        }
        return null;
    }

    public @Nullable String getCabinet() {
        if (data.has("cabinet")) {
            var cabinet = data.getString("cabinet");
            if (!cabinet.isBlank()) {
                return cabinet.replace("\n", " ").replace("||", " // ");
            }
        }
        return null;
    }

    public @Nullable String getTime() {
        if (data.has("time")) {
            return data.getString("time");
        }
        return null;
    }

    public @NotNull StringBuilder getFormattedLesson() {
        if (getSubject() != null && getCabinet() != null) {
            return new StringBuilder().append("<b>Предмет:</b> ").append(getSubject()).append("\n").append(
                    "<b>Кабинет:</b> ").append(getCabinet());
        } else {
            return new StringBuilder("Занятий по числителю нет");
        }
    }

    public @NotNull StringBuilder getActiveFormattedLesson() {
        if (getSubject() != null && getCabinet() != null) {
            return new StringBuilder().append(" → <b>Предмет:</b> ").append(getSubject()).append("\n").append(
                    " → <b>Кабинет:</b> ").append(getCabinet());
        } else {
            return new StringBuilder("Занятий по числителю нет");
        }
    }
}
