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

import java.io.File;

public class FilesPathes {

    /**
     * Константа с названием папки для файлов бота
     */
    public static final String FOLDER = "PKGHBotFiles";

    private FilesPathes() {}

    /**
     * Метод, позволяющий получить путь для необходимого файла
     *
     * @param fileName Имя файла {@link files}
     * @param fileType Тип файла {@link types}
     * @return String путь, формата PKGHBotFiles/файл.тип
     */
    public static String getPath(files fileName, types fileType) {
        return FOLDER + File.separator + fileName.toString().toLowerCase() + "." + fileType.toString().toLowerCase();
    }

    /**
     * Метод, позволяющий получить необходимый файл в папке PKGHBotFiles
     *
     * @param fileName Имя файла {@link files}
     * @param fileType Тип файла {@link types}
     * @return Возвращает указанный файл
     */
    public static File getFile(files fileName, types fileType) {
        return new File(FOLDER + File.separator + fileName.toString().toLowerCase() + "." + fileType.toString().toLowerCase());
    }

    /**
     * Enum, содержащий имена файлов
     */
    public enum files {
        SPLASHES,
        CONFIG,
        TEXT,
        SPONSOR,
        MESSAGES
    }

    /**
     * Enum, содержащий типы файлов
     */
    public enum types {
        JAR,
        TXT,
        JSON,
        PROPERTIES,
        YML,
        BSON
    }
}
