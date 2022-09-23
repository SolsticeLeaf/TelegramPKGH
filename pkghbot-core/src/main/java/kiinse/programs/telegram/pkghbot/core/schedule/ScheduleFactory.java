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

package kiinse.programs.telegram.pkghbot.core.schedule;

import kiinse.programs.telegram.pkghbot.api.schedule.Group;
import kiinse.programs.telegram.pkghbot.core.data.Config;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class ScheduleFactory {

    private ScheduleFactory() {}

    public static boolean hasGroup(@NotNull String group) {
        try {
            var array = new JSONArray(get("groups"));
            for (var i = 0; i < array.length(); i++) {
                if (Objects.equals(array.getJSONObject(i).getString("name"), group)
                    && String.valueOf(group.charAt(2)).equals("-") && String.valueOf(group.charAt(5)).equals("-")) {
                    return true;
                }
            }
            return false;
        } catch (Exception e) {
            return false;
        }
    }

    public static @Nullable Group getGroup(@NotNull String group) {
        try {
            if (hasGroup(group)) {
                return new Group(new JSONObject(get("schedule/" + URLEncoder.encode(group, StandardCharsets.UTF_8))), group) {};
            }
        } catch (Exception e) {
            return null;
        }
        return null;
    }

    private static @NotNull String get(@NotNull String url) throws IOException {
        var bufferedReader = new BufferedReader(new InputStreamReader(new URL(Config.getProperty("rest") + url).openConnection().getInputStream()));
        var sb = new StringBuilder();
        while (bufferedReader.ready()) {
            sb.append(bufferedReader.readLine());
        }
        return sb.toString();
    }
}
