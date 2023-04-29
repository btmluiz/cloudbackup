package dev.nardole.cloudbackup.forge;

import dev.nardole.cloudbackup.CloudBackup;
import me.shedaniel.architectury.platform.forge.EventBuses;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CloudBackup.MOD_ID)
public class CloudBackupForge {
    public CloudBackupForge() {
        // Submit our event bus to let architectury register our content on the right time
        EventBuses.registerModEventBus(CloudBackup.MOD_ID, FMLJavaModLoadingContext.get().getModEventBus());
        CloudBackup.init();
    }
}