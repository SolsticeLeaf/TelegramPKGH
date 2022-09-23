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

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Класс получения параметров из файла PKGHSchedule/config.properties
 *
 * @author kiinse
 * @version 3.1.2
 * @since 2.0.0
 */
public class Config {

    private Config() {}

    /**
     * Копирует файл конфигурации из jar в папку PKGHBotFiles/config.properties
     *
     * @throws IllegalStateException При первичном создании файла конфигурации
     */
    public static void export() throws Exception {
        boolean copy = FileManager.copyFile(FileManager.accessFile("config.properties"),
                                            FilesPathes.getFile(FilesPathes.files.CONFIG,
                                                                FilesPathes.types.PROPERTIES));
        if (copy) {
            throw new IllegalStateException("A new configuration file has been created. Please enter your data into it.");
        }
    }

    /**
     * Метод получения параметров с файла конфигурации
     *
     * @param key Название параметра
     * @return Значение у данного параметра
     */
    public static @NotNull String getProperty(@NotNull String key) {
        try {
            var property = new Properties();
            property.load(new FileInputStream(FilesPathes.getPath(FilesPathes.files.CONFIG,
                                                                  FilesPathes.types.PROPERTIES)));
            return property.getProperty(key);
        } catch (IOException e) {
            Sentry.captureException(e);
        }
        return key;
    }

    /**
     * Метод получения параметров с файла конфигурации
     *
     * @param key Название параметра
     * @return Значение у данного параметра
     */
    public static boolean getBoolean(@NotNull String key) {
        var property = getProperty(key).toLowerCase();
        if (property.isBlank()) {
            return false;
        }
        return !property.equalsIgnoreCase("null") && !property.equalsIgnoreCase("false");
    }

    /**
     * Метод получения версии программы из файла info.properties внутри jar
     *
     * @param version Версия API или бота {@link Version}
     * @return Версия программы
     */
    public static @NotNull String getVersion(@NotNull Config.Version version) {
        try {
            var info = FileManager.accessFile("info.properties");
            var property = new Properties();
            property.load(info);
            return property.getProperty(version.toString().toLowerCase() + ".version");
        } catch (IOException e) {
            Sentry.captureException(e);
        }
        return version.toString();
    }

    /** Enum содержащий типы версий для получения из info.properties */
    public enum Version {
        BOT,
        API
    }
}
