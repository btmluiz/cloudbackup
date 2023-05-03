package dev.nardole.cloudbackup.client.screens.storages;

import dev.nardole.cloudbackup.storages.CloudStorage;
import net.minecraft.client.gui.screens.Screen;

public class StorageScreenFactory {
    public static AbstractStorageConfigureScreen invoke(CloudStorage storage, Screen lastScreen) {
        switch (storage) {
            case GOOGLE_DRIVE:
                return new GoogleDriveConfigureScreen(lastScreen);
//            case DROPBOX:
//                return new DropboxConfigureScreen(lastScreen);
            default:
                return null;
        }
    }
}
