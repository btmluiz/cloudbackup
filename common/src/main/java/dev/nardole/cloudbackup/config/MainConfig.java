package dev.nardole.cloudbackup.config;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class MainConfig {
    public boolean enableBackup = true;

    public boolean autoBackup = true;

    public int backupInterval = 60;

    public boolean backupWhenExit = true;

    public boolean saveTimeLeft = true;

    public boolean broadCastBackupMessagesToOps = true;

    public String outputPath = "cloudbackups";

    public int getTimer() {
        return backupInterval * 60 * 1000;
    }

    public Path getOutputPath() {
        try {
            return Paths.get(outputPath).toRealPath();
        } catch (IOException e) {
            return Paths.get(outputPath);
        }
    }
}
