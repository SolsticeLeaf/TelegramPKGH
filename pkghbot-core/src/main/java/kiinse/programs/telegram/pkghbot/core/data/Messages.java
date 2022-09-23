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

package kiinse.programs.telegram.pkghbot.core.data;

import io.sentry.Sentry;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class Messages {

    /**
     * Метод взятия сообщений из файла
     *
     * @param msg Enum, определяющий сообщение {@link Message}
     * @return Возвращает сообщение из файла
     */
    public static @NotNull String getText(@NotNull Message msg) {
        try {
            var jsonFile = (JSONObject) new JSONParser().parse(new InputStreamReader(new FileInputStream(FilesPathes.getPath(
                    FilesPathes.files.MESSAGES,
                    FilesPathes.types.JSON)), StandardCharsets.UTF_8));
            return jsonFile.get(msg.toString().toLowerCase()).toString();
        } catch (IOException | ParseException e) {
            Sentry.captureException(e);
        }
        return msg.toString();
    }

    private Messages() {}

    public enum Message {
        NOPERMISSIONSMSG,
        BANMSG,
        UNBANMSG,
        BANTOADMINMSG,
        YOUBANNEDMSG,
        SPAMALERTMSG,
        GROUPSAVEDMSG,
        GROUPNOTFOUNDMSG,
        ERRORMSG,
        EMPTYMESSAGEMSG,
        USERNOTFOUNDMSG,
        ADMINREMOVEDMSG,
        ADMINADDEDMSG,
        POSTNOTFOUND,
        POSTACCEPTED,
        POSTREJECTED,
        POSTCANCELED,
        POSTCREATED,
        POSTALREADYACCEPTED,
        POSTALREADYREJECTED,
        POSTALREADYCANCELED,
        POSTALREADYCREATED,
        MESSAGENOTFOUND,
        MESSAGEALREADYSENDED,
        MESSAGEALREADYCANCELED
    }
}
