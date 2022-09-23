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

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * Класс создания файлов в папку PKGHSchedule из resources внутри jar
 *
 * @author kiinse
 * @since 3.1.1
 */
public class FileManager {

    /**
     * Метод для чтения файлов внутри jar.
     *
     * @param resource Имя нужного файла
     * @return Возвращает InputStream указанного файла
     */
    public static InputStream accessFile(String resource) {
        InputStream input = FileManager.class.getResourceAsStream(File.separator + "resources" + File.separator + resource);
        if (input == null) {
            input = FileManager.class.getClassLoader().getResourceAsStream(resource);
        }
        return input;
    }

    /**
     * Метод копирования файлов
     *
     * @param sourceFile Входной файл
     * @param destFile   Выходной файл
     * @throws IOException В случае невозможности получения доступа к файлу
     */
    public static boolean copyFile(InputStream sourceFile, File destFile) throws IOException {
        if (!destFile.exists()) {
            FileUtils.copyInputStreamToFile(sourceFile, destFile);
            return true;
        }
        return false;
    }

    private FileManager() {}
}

