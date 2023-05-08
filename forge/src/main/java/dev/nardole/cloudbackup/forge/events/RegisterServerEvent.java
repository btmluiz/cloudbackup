package dev.nardole.cloudbackup.forge.events;

import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.threads.BackupThread;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.event.server.ServerStoppedEvent;

@Mod.EventBusSubscriber
public class RegisterServerEvent {

    @SubscribeEvent
    public void onServerStarted(ServerStartedEvent event) {
        MinecraftServer server = event.getServer();

        server.addTickable(() -> {
            if (!server.getPlayerList().getPlayers().isEmpty()
                    && server.isSingleplayer()
                    && CloudBackup.getConfig().autoBackup) {
                boolean done = BackupThread.tryCreateBackup(server);

                if (done) {
                    CloudBackup.LOGGER.info("Backup done.");
                }
            }
        });
    }

    @SubscribeEvent
    public void onServerStopped(ServerStoppedEvent event) {
        if (CloudBackup.getConfig().backupWhenExit) {
            MinecraftServer server = event.getServer();
            boolean done = BackupThread.tryCreateBackup(server);

            if (done) {
                CloudBackup.LOGGER.info("Backup done.");
            }
        }
    }

    @SubscribeEvent
    public void onRegisterCommandEvent(RegisterCommandsEvent event) {
        event.getDispatcher().register(Commands.literal("cloudbackup")
                .requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
                .then(Commands.literal("run")
                        .executes(context -> {
                            MinecraftServer server = context.getSource().getServer();
                            BackupThread.createBackup(server, false);
                            return 1;
                        }))
                .then(Commands.literal("lastBackup")
                        .executes(context -> {
                            MinecraftServer server = context.getSource().getServer();

                            context.getSource().sendSuccess(BackupThread.getLastBackupDateFormatted(server), false);
                            return 1;
                        })));
    }
}
