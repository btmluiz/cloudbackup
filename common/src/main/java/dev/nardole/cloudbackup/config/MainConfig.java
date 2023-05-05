package dev.nardole.cloudbackup.config;

import dev.nardole.cloudbackup.storages.CloudStorage;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

@FileName("config")
public class MainConfig implements Cloneable {
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

//    public DropboxConfig dropbox = new DropboxConfig();

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

    public StorageConfig getStorageConfig(CloudStorage storage) {
        switch (storage) {
            case GOOGLE_DRIVE:
                return googleDrive;
//            case DROPBOX:
//                return dropbox;
            default:
                throw new RuntimeException("Unknown storage type");
        }
    }

    @Override
    public MainConfig clone() {
        try {
            MainConfig clone = (MainConfig) super.clone();
            clone.googleDrive = (GoogleDriveConfig) googleDrive.clone();
//            clone.dropbox = (DropboxConfig) dropbox.clone();
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

    public abstract static class StorageConfig implements Cloneable {
        public boolean enabled = false;

        public String uploadDir = "cloudbackups";

        public boolean makeWorldDir = true;

        @Override
        public StorageConfig clone() {
            try {
                return (StorageConfig) super.clone();
            } catch (CloneNotSupportedException e) {
                throw new AssertionError();
            }
        }
    }

    public static class GoogleDriveConfig extends StorageConfig {
    }

//    public static class DropboxConfig extends StorageConfig {
//
//        @Override
//        public CloudStorage getStorage() {
//            return CloudStorage.DROPBOX;
//        }
//    }
}
