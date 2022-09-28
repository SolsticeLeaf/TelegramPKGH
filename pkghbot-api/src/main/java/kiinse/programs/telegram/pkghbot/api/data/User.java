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

package kiinse.programs.telegram.pkghbot.api.data;

import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import org.jetbrains.annotations.NotNull;

public interface User {

    @NotNull User setGroupChat(boolean groupChat);

    @NotNull String getName();

    @NotNull User setName(@NotNull String name);

    @NotNull String getGroup();

    long getChatId();

    @NotNull User setChatId(long chatId);

    @NotNull UserStatus getStatus();

    @NotNull User setStatus(@NotNull UserStatus userStatus);

    boolean isMailing();

    @NotNull User setMailing(boolean mailing);

    boolean isLessonMailing();

    @NotNull User setLessonMailing(boolean lessonMailing);

    boolean isAdminMailing();

    @NotNull User setAdminMailing(boolean adminMailing);

    boolean isAdmin();

    @NotNull User setAdmin(boolean admin);

    boolean isGroup();

    @NotNull User setGroup(@NotNull String group);

    @NotNull String getRegistrationDate();

    @NotNull User setRegistrationDate(@NotNull String date);

    @NotNull User upload();

    @NotNull String toString();

}
