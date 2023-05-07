package dev.nardole.cloudbackup;

import dev.nardole.cloudbackup.config.ConfigHandler;
import dev.nardole.cloudbackup.config.MainConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudBackup {
    public static final String MOD_ID = "cloudbackup";
    public static final Logger LOGGER = LogManager.getLogger(CloudBackup.class);
    private static MainConfig MAIN_CONFIG = ConfigHandler.loadConfig(MainConfig.class);

    public static void init() {
    }

    public static MainConfig getConfig() {
        return MAIN_CONFIG;
    }

    public static void reloadConfig() {
        MAIN_CONFIG = ConfigHandler.loadConfig(MainConfig.class);
    }
}