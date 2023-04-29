package dev.nardole.cloudbackup;


import dev.architectury.injectables.annotations.ExpectPlatform;

import java.io.File;
import java.nio.file.Path;

public class CloudBackupConfig {

    public static File getConfigFile(String name) {
        return new File(getConfigDir().toFile(), name);
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }
}
