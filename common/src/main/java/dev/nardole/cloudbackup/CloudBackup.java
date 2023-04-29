package dev.nardole.cloudbackup;

import dev.nardole.cloudbackup.config.MainConfig;
import dev.nardole.cloudbackup.data.BackupData;
import dev.nardole.cloudbackup.threads.BackupThread;
import me.shedaniel.architectury.event.events.CommandRegistrationEvent;
import me.shedaniel.architectury.event.events.LifecycleEvent;
import net.minecraft.commands.Commands;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudBackup {
    public static final String MOD_ID = "cloudbackup";
    private static BackupData backupData;
    public static final Logger LOGGER = LogManager.getLogger(CloudBackup.class);
    private static final MainConfig MAIN_CONFIG = new MainConfig();

    public static void init() {
        LOGGER.info(CloudBackupConfig.getConfigDir().toAbsolutePath().normalize().toString());

        CommandRegistrationEvent.EVENT.register(((dispatcher, selection) -> {
            dispatcher.register(Commands.literal("cloudbackup")
                    .requires(source -> source.getServer().isSingleplayer() || source.hasPermission(2))
                    .then(Commands.literal("run")
                            .executes(context -> {
                                MinecraftServer server = context.getSource().getServer();
                                BackupThread.createBackup(server, false);
                                return 1;
                            })));
        }));

        LifecycleEvent.SERVER_STARTED.register(server -> {
            server.addTickable(() -> {
                if (!server.getPlayerList().getPlayers().isEmpty()) {
                    boolean done = BackupThread.tryCreateBackup(server);

                    if (done) {
                        CloudBackup.LOGGER.info("Backup done.");
                    }
                }
            });
        });

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
}