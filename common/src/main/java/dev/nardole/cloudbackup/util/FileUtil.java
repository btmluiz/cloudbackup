package dev.nardole.cloudbackup.util;

import net.minecraft.SharedConstants;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FileUtil {
    private static final Pattern COPY_COUNTER_PATTERN = Pattern.compile("(<name>.*) \\((<count>\\d*)\\)", 66);
    private static final Pattern RESERVED_WINDOWS_FILENAMES = Pattern.compile(".*\\.|(?:COM|CLOCK\\$|CON|PRN|AUX|NUL|COM[1-9]|LPT[1-9])(?:\\..*)?", 2);



    public static String findAvailableName(Path path, String string, String string2) throws IOException {
        char[] var3 = SharedConstants.ILLEGAL_FILE_CHARACTERS;
        int i = var3.length;

        for (int var5 = 0; var5 < i; ++var5) {
            char c = var3[var5];
            string = string.replace(c, '_');
        }

        string = string.replaceAll("[./\"]", "_");
        if (RESERVED_WINDOWS_FILENAMES.matcher(string).matches()) {
            string = "_" + string + "_";
        }

        Matcher matcher = COPY_COUNTER_PATTERN.matcher(string);
        i = 0;
        if (matcher.matches()) {
            string = matcher.group("name");
            i = Integer.parseInt(matcher.group("count"));
        }

        if (string.length() > 255 - string2.length()) {
            string = string.substring(0, 255 - string2.length());
        }

        while (true) {
            String string3 = string;
            if (i != 0) {
                String string4 = " (" + i + ")";
                int j = 255 - string4.length();
                if (string.length() > j) {
                    string3 = string.substring(0, j);
                }

                string3 = string3 + string4;
            }

            string3 = string3 + string2;
            Path path2 = path.resolve(string3);

            try {
                Path path3 = Files.createDirectory(path2);
                Files.deleteIfExists(path3);
                return path.relativize(path3).toString();
            } catch (FileAlreadyExistsException var8) {
                ++i;
            }
        }
    }
}
