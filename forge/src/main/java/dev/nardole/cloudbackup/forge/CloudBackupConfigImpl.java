package dev.nardole.cloudbackup.forge;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class CloudBackupConfigImpl {

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
