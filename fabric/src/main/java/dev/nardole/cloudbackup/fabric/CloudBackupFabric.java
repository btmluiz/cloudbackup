package dev.nardole.cloudbackup.fabric;

import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.threads.BackupThread;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;

public class CloudBackupFabric implements ModInitializer {
    @Override
    public void onInitialize() {
        CloudBackup.init();

        ServerLifecycleEvents.SERVER_STARTED.register(server -> server.addTickable(() -> {
            if (!server.getPlayerList().getPlayers().isEmpty()
                    && server.isSingleplayer()
                    && CloudBackup.getConfig().autoBackup) {
                boolean done = BackupThread.tryCreateBackup(server);

                if (done) {
                    CloudBackup.LOGGER.info("Backup done.");
                }
            }
        }));

        ServerLifecycleEvents.SERVER_STOPPED.register(server -> {
            boolean done = BackupThread.tryCreateBackup(server);

            if (done) {
                CloudBackup.LOGGER.info("Backup done.");
            }
        });

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> dispatcher.register(Commands.literal("cloudbackup")
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
                        }))));
    }
}