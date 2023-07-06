package dev.nardole.cloudbackup.forge;

import dev.nardole.cloudbackup.ClothScreen;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.fml.ModLoadingContext;

public class CloudBackupCloth {
    public static void register() {
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, () -> new ConfigScreenHandler.ConfigScreenFactory((client, parent) -> ClothScreen.getConfigScreenByCloth(parent)));
    }
}
