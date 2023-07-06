package dev.nardole.cloudbackup.config;

import dev.nardole.cloudbackup.storages.CloudStorage;
import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;
import me.shedaniel.autoconfig.annotation.ConfigEntry;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@Config(name = "cloudbackup")
public class CloudBackupConfig implements ConfigData {
    @ConfigEntry.Category("general")
    public boolean enableBackup = true;

    @ConfigEntry.Category("general")
    public boolean autoBackup = true;

    /**
     * Interval in minutes
     */
    @ConfigEntry.Category("general")
    @ConfigEntry.BoundedDiscrete(min = 30, max = 3600)
    public int backupInterval = 30;

    @ConfigEntry.Category("general")
    public boolean backupWhenExit = true;

    @ConfigEntry.Category("general")
    public boolean broadCastBackupMessage = true;

    @ConfigEntry.Category("general")
    public String outputPath = "cloudbackups";

    @ConfigEntry.Category("googleDrive")
    @ConfigEntry.Gui.TransitiveObject
    public StorageConfig googleDrive = new StorageConfig();

    public static class StorageConfig {
        public boolean enabled = false;

        public String uploadDir = "cloudbackups";

        public boolean makeWorldDir = true;
    }

    public Path getOutputPath() {
        try {
            return Paths.get(outputPath).toRealPath();
        } catch (IOException e) {
            return Paths.get(outputPath);
        }
    }

    public int getTimer() {
        return backupInterval * 60 * 1000;
    }

    public StorageConfig getStorageConfig(CloudStorage storage) {
        switch (storage) {
            case GOOGLE_DRIVE:
                return googleDrive;
            default:
                throw new RuntimeException("Unknown storage type");
        }
    }
}
