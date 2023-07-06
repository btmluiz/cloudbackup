package dev.nardole.cloudbackup;


import dev.architectury.injectables.annotations.ExpectPlatform;

import java.nio.file.Path;

@Deprecated(since = "1.0.7")
public class CloudBackupConfig {
    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }
}
