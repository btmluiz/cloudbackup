package dev.nardole.cloudbackup.fabric;

import net.fabricmc.loader.api.FabricLoader;

import java.nio.file.Path;

public class CloudBackupConfigImpl {

    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }
}
