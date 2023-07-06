package dev.nardole.cloudbackup;

import dev.nardole.cloudbackup.client.gui.builders.ButtonBuilder;
import dev.nardole.cloudbackup.client.screens.storages.GoogleDriveConfigureScreen;
import dev.nardole.cloudbackup.config.CloudBackupConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class ClothScreen {

    public static Screen getConfigScreenByCloth(Screen parent) {
        ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.translatable("cloudbackup.config.title"));
        ConfigEntryBuilder entryBuilder = builder.entryBuilder();

        CloudBackupConfig config = CloudBackup.loadConfig();

        ConfigCategory general = builder.getOrCreateCategory(Component.translatable("cloudbackup.config.category.general"));
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("cloudbackup.config.option.enableBackup"), config.enableBackup)
                .setDefaultValue(config.enableBackup)
                .setSaveConsumer(newValue -> config.enableBackup = newValue)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("cloudbackup.config.option.autoBackup"), config.autoBackup)
                .setDefaultValue(config.autoBackup)
                .setSaveConsumer(newValue -> config.autoBackup = newValue)
                .build());
        general.addEntry(entryBuilder.startIntField(Component.translatable("cloudbackup.config.option.backupInterval"), config.backupInterval)
                .setDefaultValue(config.backupInterval)
                .setSaveConsumer(newValue -> config.backupInterval = newValue)
                .build());
        general.addEntry(entryBuilder.startBooleanToggle(Component.translatable("cloudbackup.config.option.backupWhenExit"), config.backupWhenExit)
                .setDefaultValue(config.backupWhenExit)
                .setSaveConsumer(newValue -> config.backupWhenExit = newValue)
                .build());
        general.addEntry(entryBuilder.startStrField(Component.translatable("cloudbackup.config.option.outputPath"), config.outputPath)
                .setDefaultValue(config.outputPath)
                .setSaveConsumer(newValue -> config.outputPath = newValue)
                .build());

        ConfigCategory googleDriveCategory = builder.getOrCreateCategory(Component.translatable("cloudbackup.config.category.googleDrive"));
        googleDriveCategory.addEntry((new ButtonBuilder(Component.translatable("cloudbackup.config.option.googleDrive.authentication")))
                .setOnClick((widget) -> Minecraft.getInstance().setScreen(new GoogleDriveConfigureScreen(Minecraft.getInstance().screen))).setButtonText(() -> Component.translatable("cloudbackup.config.option.googleDrive.configure"))
                .build());
        googleDriveCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("cloudbackup.config.option.googleDrive.enabled"), config.googleDrive.enabled)
                .setDefaultValue(config.googleDrive.enabled)
                .setSaveConsumer(newValue -> config.googleDrive.enabled = newValue)
                .build());
        googleDriveCategory.addEntry(entryBuilder.startStrField(Component.translatable("cloudbackup.config.option.googleDrive.uploadDir"), config.googleDrive.uploadDir)
                .setDefaultValue(config.googleDrive.uploadDir)
                .setSaveConsumer(newValue -> config.googleDrive.uploadDir = newValue)
                .build());
        googleDriveCategory.addEntry(entryBuilder.startBooleanToggle(Component.translatable("cloudbackup.config.option.googleDrive.makeWorldDir"), config.googleDrive.makeWorldDir)
                .setDefaultValue(config.googleDrive.makeWorldDir)
                .setSaveConsumer(newValue -> config.googleDrive.makeWorldDir = newValue)
                .build());


        return builder.setSavingRunnable(() -> {
            CloudBackup.saveConfig(config);
        }).build();
    }
}
