// MIT License
//
// Copyright (c) 2022 kiinse
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NON INFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package kiinse.programs.telegram.pkghbot.api.files;

import kiinse.programs.telegram.pkghbot.api.utils.LoggerUtils;
import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

@SuppressWarnings("unused")
public class JsonFile extends FilesManager {

    private final File file;

    public JsonFile(@NotNull FilesKeys fileName) {
        if (isFileNotExists(fileName)) {
            copyFile(fileName);
        }
        this.file = getFile(fileName);
    }

    public @NotNull JSONObject getJsonFromFile() throws IOException {
        try (var br = new BufferedReader(new FileReader(file.getAbsolutePath()))) {
            var line = br.readLine();
            if (line == null) {
                return new JSONObject();
            }
            var json = new JSONObject(Files.readString(Paths.get(file.getAbsolutePath())));
            LoggerUtils.getLogger().info("File '&b" + file.getName() + "&a' loaded");
            return json;
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

    public void saveJsonToFile(@NotNull JSONObject json) throws IOException {
        if (!file.exists() && file.createNewFile()) {
            LoggerUtils.getLogger().info("File '&b" + file.getName() + "&a' created");
        }
        var lines = List.of(json.toString());
        Files.write(Paths.get(file.getAbsolutePath()), lines, StandardCharsets.UTF_8);
        LoggerUtils.getLogger().info("File '&b" + file.getName() + "&a' saved");
    }

}
