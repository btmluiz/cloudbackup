package dev.nardole.cloudbackup.config;

@Deprecated(since = "1.0.7")
@FileName("config")
public class MainConfig {
    public boolean enableBackup = true;

    public boolean autoBackup = true;

    /**
     * Interval in minutes
     */
    public int backupInterval = 30;

    public boolean backupWhenExit = true;

    public boolean broadCastBackupMessage = true;

    public String outputPath = "cloudbackups";

    public GoogleDriveConfig googleDrive = new GoogleDriveConfig();

    public abstract static class StorageConfig {
        public boolean enabled = false;

        public String uploadDir = "cloudbackups";

        public boolean makeWorldDir = true;
    }

    public static class GoogleDriveConfig extends StorageConfig {
    }
}
