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

package kiinse.programs.telegram.pkghbot.core.mongo.data;

import kiinse.programs.telegram.pkghbot.api.data.User;
import kiinse.programs.telegram.pkghbot.api.data.enums.UserStatus;
import kiinse.programs.telegram.pkghbot.core.mongo.UserQuery;
import org.jetbrains.annotations.NotNull;

import java.text.SimpleDateFormat;
import java.util.Date;

public class BotUser implements User {
    private String name;
    private String group;
    private long chatId;
    private UserStatus userStatus;
    private boolean mailing;
    private boolean lessonMailing;
    private boolean admin;
    private boolean adminMailing;
    private boolean groupChat;
    private String registrationDate;

    public BotUser() {
        this.name = "no username";
        this.group = "no group";
        this.chatId = 0;
        this.userStatus = UserStatus.REGISTRATION;
        this.mailing = false;
        this.lessonMailing = false;
        this.admin = false;
        this.adminMailing = false;
        this.groupChat = false;
        this.registrationDate = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss").format(new Date());
    }

    @Override
    public @NotNull User setName(@NotNull String name) {
        this.name = name;
        return this;
    }

    @Override
    public @NotNull User setGroup(@NotNull String group) {
        this.group = group;
        return this;
    }

    @Override
    public @NotNull User setChatId(long chatId) {
        this.chatId = chatId;
        return this;
    }

    @Override
    public @NotNull User setStatus(@NotNull UserStatus userStatus) {
        this.userStatus = userStatus;
        return this;
    }

    @Override
    public @NotNull User setMailing(boolean mailing) {
        this.mailing = mailing;
        return this;
    }

    @Override
    public @NotNull User setLessonMailing(boolean lessonMailing) {
        this.lessonMailing = lessonMailing;
        return this;
    }

    @Override
    public @NotNull User setAdminMailing(boolean adminMailing) {
        this.adminMailing = adminMailing;
        return this;
    }

    @Override
    public @NotNull User setAdmin(boolean admin) {
        this.admin = admin;
        return this;
    }

    @Override
    public @NotNull User setGroupChat(boolean groupChat) {
        this.groupChat = groupChat;
        return this;
    }

    @Override
    public @NotNull User setRegistrationDate(@NotNull String date) {
        this.registrationDate = date;
        return this;
    }

    @Override
    public @NotNull String getName() {
        return name;
    }

    @Override
    public @NotNull String getGroup() {
        return group;
    }

    @Override
    public long getChatId() {
        return chatId;
    }

    @Override
    public @NotNull UserStatus getStatus() {
        return userStatus;
    }

    @Override
    public boolean isMailing() {
        return mailing;
    }

    @Override
    public boolean isLessonMailing() {
        return lessonMailing;
    }

    @Override
    public boolean isAdminMailing() {
        return adminMailing;
    }

    @Override
    public boolean isAdmin() {
        return admin;
    }

    @Override
    public boolean isGroup() {
        return groupChat;
    }

    @Override
    public @NotNull String getRegistrationDate() {
        return registrationDate;
    }

    @Override
    public @NotNull User upload() {
        return UserQuery.updateUser(this);
    }
}
