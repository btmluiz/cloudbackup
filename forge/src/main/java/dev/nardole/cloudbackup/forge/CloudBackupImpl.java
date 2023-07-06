package dev.nardole.cloudbackup.forge;

import dev.nardole.cloudbackup.CloudBackup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.DistExecutor;

public class CloudBackupImpl {

    public static void register() {
        try {
            DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CloudBackupCloth::register);
        } catch (Exception e) {
            CloudBackup.LOGGER.error("Failed to register cloth config screen", e);
        }
    }
}
