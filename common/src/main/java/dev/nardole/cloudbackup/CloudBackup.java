package dev.nardole.cloudbackup;

import com.mojang.logging.LogUtils;
import dev.nardole.cloudbackup.config.CloudBackupConfig;
import dev.nardole.cloudbackup.config.ConfigHandler;
import dev.nardole.cloudbackup.config.MainConfig;
import me.shedaniel.autoconfig.AutoConfig;
import me.shedaniel.autoconfig.serializer.GsonConfigSerializer;
import org.slf4j.Logger;

public class CloudBackup {
    public static final String MOD_ID = "cloudbackup";
    public static final Logger LOGGER = LogUtils.getLogger();

    @Deprecated(since = "1.0.7", forRemoval = true)
    private static MainConfig MAIN_CONFIG = ConfigHandler.loadConfig(MainConfig.class);

    public static void init() {
        AutoConfig.register(CloudBackupConfig.class, GsonConfigSerializer::new);

        /*
            Migrate old config
            Will be removed in 1.1.0
         */
        migrateOldConfig();
    }

    public static void saveConfig(CloudBackupConfig config) {
        AutoConfig.getConfigHolder(CloudBackupConfig.class).setConfig(config);
        AutoConfig.getConfigHolder(CloudBackupConfig.class).save();
    }

    public static CloudBackupConfig loadConfig() {
        return AutoConfig.getConfigHolder(CloudBackupConfig.class).getConfig();
    }

    public static void migrateOldConfig() {
        if (MAIN_CONFIG == null) {
            return;
        }

        CloudBackupConfig config = loadConfig();
        config.enableBackup = MAIN_CONFIG.enableBackup;
        config.autoBackup = MAIN_CONFIG.autoBackup;
        config.backupInterval = MAIN_CONFIG.backupInterval;
        config.backupWhenExit = MAIN_CONFIG.backupWhenExit;
        config.broadCastBackupMessage = MAIN_CONFIG.broadCastBackupMessage;
        config.outputPath = MAIN_CONFIG.outputPath;
        config.googleDrive.enabled = MAIN_CONFIG.googleDrive.enabled;
        config.googleDrive.uploadDir = MAIN_CONFIG.googleDrive.uploadDir;
        config.googleDrive.makeWorldDir = MAIN_CONFIG.googleDrive.makeWorldDir;
        saveConfig(config);
    }
}