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

package kiinse.programs.telegram.pkghbot.api.posts;

import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostSettings;
import kiinse.programs.telegram.pkghbot.api.posts.enums.PostStatus;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public interface Post {

    @NotNull String getText();

    @NotNull Post setText(@NotNull String text);

    @NotNull User getUser();

    @NotNull Post setUser(@NotNull User user);

    @NotNull UUID getId();

    @NotNull PostSettings getSettings();

    @NotNull Post setSettings(@NotNull PostSettings settings);

    @NotNull PostStatus getStatus();

    @NotNull Post setStatus(@NotNull PostStatus status);

    @NotNull String getCreationDate();

    @NotNull Post setCreationDate(@NotNull String date);

    @NotNull Post upload();

    //TODO: Видео, Фото
}
