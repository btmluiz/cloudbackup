package dev.nardole.cloudbackup;

import dev.architectury.event.events.common.CommandRegistrationEvent;
import dev.architectury.event.events.common.LifecycleEvent;
import dev.nardole.cloudbackup.config.ConfigHandler;
import dev.nardole.cloudbackup.config.MainConfig;
import dev.nardole.cloudbackup.threads.BackupThread;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudBackup {
    public static final String MOD_ID = "cloudbackup";
    public static final Logger LOGGER = LogManager.getLogger(CloudBackup.class);
    private static MainConfig MAIN_CONFIG = ConfigHandler.loadConfig(MainConfig.class);

    public static void init() {
        CommandRegistrationEvent.EVENT.register(((dispatcher, selection) -> dispatcher.register(Commands.literal("cloudbackup")
                .requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
                .then(Commands.literal("run")
                        .executes(context -> {
                            MinecraftServer server = context.getSource().getServer();
                            BackupThread.createBackup(server, false);
                            return 1;
                        })))));

        LifecycleEvent.SERVER_STARTED.register(server -> server.addTickable(() -> {
            if (!server.getPlayerList().getPlayers().isEmpty()
                    && server.isSingleplayer()
                    && MAIN_CONFIG.autoBackup) {
                boolean done = BackupThread.tryCreateBackup(server);

                if (done) {
                    CloudBackup.LOGGER.info("Backup done.");
                }
            }
        }));

        LifecycleEvent.SERVER_STOPPED.register(server -> {
            boolean done = BackupThread.tryCreateBackup(server);

            if (done) {
                CloudBackup.LOGGER.info("Backup done.");
            }
        });
    }

    public static MainConfig getConfig() {
        return MAIN_CONFIG;
    }

    public static void reloadConfig() {
        MAIN_CONFIG = ConfigHandler.loadConfig(MainConfig.class);
    }
}