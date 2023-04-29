package dev.nardole.cloudbackup.fabric;

import dev.nardole.cloudbackup.CloudBackup;
import net.fabricmc.api.ModInitializer;

public class CloudBackupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CloudBackup.init();
    }
}