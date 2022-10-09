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

package kiinse.programs.telegram.pkghbot.api.files;

import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@SuppressWarnings("unused")
public abstract class FilesManager {

    public void createFile(@NotNull FilesKeys file) throws IOException {
        var destFile = getFile(file);
        if (destFile.createNewFile())
            LoggerUtils.getLogger().debug("File '{}' created", destFile.getName());
    }

    public void createDirectory(@NotNull DirectoriesKeys directory) throws SecurityException {
        var destDirectory = getFile(directory);
        if (destDirectory.exists()) deleteFile(directory);
        if (destDirectory.mkdirs())
            LoggerUtils.getLogger().debug("Directory '{}' created", destDirectory.getName());
    }

    public void copyFile(@NotNull FilesKeys file) {
        copyFileMethod(file, file);
    }

    public void copyFile(@NotNull FilesKeys oldFile, @NotNull FilesKeys newFile) {
        copyFileMethod(oldFile, newFile);
    }

    private void copyFileMethod(@NotNull FilesKeys oldFile, @NotNull FilesKeys newFile) {
        var destFile = getFile(newFile);
        if (!destFile.exists()) {
            var inputStream = accessFile(oldFile);
            if (inputStream != null) {
                try {
                    FileUtils.copyInputStreamToFile(inputStream, destFile);
                    LoggerUtils.getLogger().debug("File '{}' created", destFile.getName());
                } catch (IOException e) {
                    LoggerUtils.getLogger().warn("Error on copying file '{}'! Message: {}", destFile.getName(), e.getMessage());
                }
            } else {
                LoggerUtils.getLogger().warn("File '{}' not found inside plugin jar. Creating a new file...", getFileName(oldFile));
                try {
                    createFile(newFile);
                } catch (IOException e) {
                    LoggerUtils.getLogger().error("Error on creating file '{}'! Message: {}", destFile.getName(), e.getMessage());
                }
            }
        }
    }

    public void copyFile(@NotNull DirectoriesKeys directory) {
        var destDirectory = getFile(directory);
        if (!destDirectory.exists() || listFilesInDirectory(directory).isEmpty()) {
            try {
                createDirectory(directory);
                for (var file : getFilesInDirectoryInJar(directory)) {
                    FileUtils.copyInputStreamToFile(Objects.requireNonNull(accessFile(getFileName(directory) + "/" + file)),
                                                    new File(getDataFolder() + getFileName(directory) + File.separator + file));
                }
            } catch (Exception e) {
                LoggerUtils.getLogger().warn("Error on copying directory '{}'! Message: {}", destDirectory.getName(), e.getMessage());
                deleteFile(directory);
            }
        }
    }

    public @NotNull List<File> listFilesInDirectory(@NotNull DirectoriesKeys directory) {
        var list = new ArrayList<File>();
        Collections.addAll(list, Objects.requireNonNull(getFile(directory).listFiles()));
        return list;
    }

    public void copyFileInFolder(@NotNull FilesKeys file1, @NotNull FilesKeys file2) throws IOException {
        var file = getFile(file2);
        var oldFile = getFile(file1);
        if (!file.exists()) {
            FileUtils.copyFile(oldFile, file);
            LoggerUtils.getLogger().debug("File '{}' copied to file '{}'", oldFile.getName(), file.getName());
        }
    }

    public @NotNull File getFile(@NotNull FilesKeys file) {
        return new File(getDataFolder() + getFileName(file));
    }

    public @NotNull File getFile(@NotNull DirectoriesKeys directory) {
        return new File(getDataFolder() + getFileName(directory));
    }

    public @Nullable InputStream accessFile(@NotNull FilesKeys file) {
        var input = FilesManager.class.getResourceAsStream(getFileName(file));
        if (input == null) input = FilesManager.class.getClassLoader().getResourceAsStream(getFileName(file));
        return input;
    }

    private @Nullable InputStream accessFile(@NotNull String file) {
        var input = FilesManager.class.getResourceAsStream(file);
        if (input == null) input = FilesManager.class.getClassLoader().getResourceAsStream(file);
        return input;
    }

    public @NotNull List<String> getFilesInDirectoryInJar(@NotNull DirectoriesKeys directory) throws Exception {
        var list = new ArrayList<String>();
        for (var file : getResourceUrls("classpath:/" + getFileName(directory) + "/*.*")) {
            var split = file.split("/");
            list.add(split[split.length - 1]);
        }
        return list;
    }

    private @NotNull List<String> getResourceUrls(@NotNull String locationPattern) throws IOException {
        var resolver = new PathMatchingResourcePatternResolver(FilesManager.class.getClassLoader());
        return Arrays.stream(resolver.getResources(locationPattern))
                     .map(this::toURL)
                     .filter(Objects::nonNull)
                     .toList();
    }

    private @Nullable String toURL(@NotNull Resource r) {
        try {
            return r.getURL().toExternalForm();
        } catch (IOException e) {
            return null;
        }
    }

    public boolean isFileNotExists(@NotNull FilesKeys file) {
        return !getFile(file).exists();
    }

    public boolean isFileNotExists(@NotNull DirectoriesKeys file) {
        return !getFile(file).exists();
    }

    public boolean isDirectoryEmpty(@NotNull DirectoriesKeys file) {
        return listFilesInDirectory(file).isEmpty();
    }

    public @NotNull String getDataFolder() {
        return "PKGHBotFiles" + File.separator;
    }

    public @NotNull String getFileName(@NotNull FilesKeys key) {
        return key.toString().toLowerCase().replace("_", ".");
    }

    public @NotNull String getFileName(@NotNull DirectoriesKeys directory) {
        return directory.toString().toLowerCase();
    }

    public void deleteFile(@NotNull FilesKeys file) {
        if (getFile(file).delete())
            LoggerUtils.getLogger().debug("File '{}' deleted", getFile(file).getName());
    }

    public void deleteFile(@NotNull DirectoriesKeys file) {
        if (getFile(file).delete())
            LoggerUtils.getLogger().debug("File '{}' deleted", getFile(file).getName());
    }
}
