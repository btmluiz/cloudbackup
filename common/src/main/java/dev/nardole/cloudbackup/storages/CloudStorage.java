package dev.nardole.cloudbackup.storages;

import net.minecraft.network.chat.Component;

public enum CloudStorage {
    GOOGLE_DRIVE("google_drive", GoogleDriveStorage.class);
//    DROPBOX("dropbox", DropboxStorage.class);
    private final String name;
    private final Class<? extends IStorage> storage;

    CloudStorage(String name, Class<? extends IStorage> storage) {
        this.name = name;
        this.storage = storage;
    }

    public String getName() {
        return name;
    }

    public Class<? extends IStorage> getStorage() {
        return storage;
    }

    public Component getDisplayName() {
        return Component.translatable("cloudbackup.storage." + this.name);
    }
}
