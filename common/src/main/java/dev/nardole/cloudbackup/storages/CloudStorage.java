package dev.nardole.cloudbackup.storages;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.jetbrains.annotations.NotNull;

public enum CloudStorage {
    GOOGLE_DRIVE("google_drive", "google_drive"),
    DROPBOX("dropbox", "dropbox"),
    AWS_S3("aws_s3", "aws_s3"),
    GOOGLE_STORAGE("google_storage", "google_storage");
    private final String id;
    private final String key;

    private CloudStorage(String id, String key) {
        this.id = id;
        this.key = key;
    }

    public String getId() {
        return id;
    }

    public String getKey() {
        return key;
    }

    public Component getDisplayName() {
        return new TranslatableComponent("cloud_locations." + this.key);
    }

    @NotNull
    public String getSerializedName() {
        return this.key;
    }
}
