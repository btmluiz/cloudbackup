package dev.nardole.cloudbackup.forge;

import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.forge.events.RegisterServerEvent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLLoadCompleteEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(CloudBackup.MOD_ID)
public class CloudBackupForge {
    public CloudBackupForge() {
        CloudBackup.init();

        IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
        eventBus.addListener(this::loadComplete);

        DistExecutor.safeRunWhenOn(Dist.CLIENT, () -> CloudBackupImpl::register);
    }

    private void loadComplete(FMLLoadCompleteEvent event) {
        MinecraftForge.EVENT_BUS.register(new RegisterServerEvent());
    }
}