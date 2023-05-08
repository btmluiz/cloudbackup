package dev.nardole.cloudbackup.client.screens.storages;

import com.google.api.services.drive.model.User;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.config.ConfigHandler;
import dev.nardole.cloudbackup.config.MainConfig;
import dev.nardole.cloudbackup.storages.GoogleDriveStorage;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.security.GeneralSecurityException;

public class GoogleDriveConfigureScreen extends AbstractStorageConfigureScreen {

    public final GoogleDriveStorage googleDrive;
    public static Component TITLE = Component.translatable("gd_screen.title");
    public static final Logger LOGGER = LogManager.getLogger();

    private Component loginStatus;

    private Button connectButton;

    private Button disconnectButton;

    private EditBox uploadFolderEditBox;

    private Button makeWorldFolderButton;

    private User authUser;

    private final MainConfig config = CloudBackup.getConfig().clone();

    public GoogleDriveConfigureScreen(Screen lastScreen) {
        super(TITLE, lastScreen);

        try {
            googleDrive = new GoogleDriveStorage();
            GoogleDriveStorage.ThreadedReceiver threadedReceiver = googleDrive.getReceiver();
            threadedReceiver.start();
        } catch (Exception e) {
            LOGGER.error("Could not create GoogleDriveStorage");
            throw new RuntimeException(e);
        }
    }

    protected void init() {
        connectButton = addRenderableWidget(new Button(width / 2 - 155, height / 6 + 48 - 6, 150, 20, Component.translatable("cloudbackup.storage.open_browser"), (button -> {
            try {
                String driveUrl = googleDrive.getBrowserUrl();

                Util.getPlatform().openUri(driveUrl);
                assert minecraft != null;
                minecraft.setScreen(this);
            } catch (Exception e) {
                LOGGER.error("Could not get authorization url", e);
            }
        })));

        // Logout button
        disconnectButton = addRenderableWidget(new Button(width / 2 - 155 + 160, height / 6 + 48 - 6, 150, 20, Component.translatable("cloudbackup.storage.disconnect"), (button -> {
            try {
                googleDrive.disconnect();
                authUser = null;
            } catch (Exception e) {
                LOGGER.error("Could not logout", e);
            }
        })));

        // Upload folder
        uploadFolderEditBox = new EditBox(font, width / 2 - 155, height / 6 + 72 - 6, 150, 20, Component.translatable("cloudbackup.storage.upload_folder"));
        uploadFolderEditBox.setValue(config.googleDrive.uploadDir);
        uploadFolderEditBox.setResponder(string -> config.googleDrive.uploadDir = string);

        addWidget(uploadFolderEditBox);

        // Make world folder button
        makeWorldFolderButton = addRenderableWidget(new Button(width / 2 - 155 + 160, height / 6 + 72 - 6, 150, 20, Component.translatable("cloudbackup.storage.make_world_folder"), (button -> config.googleDrive.makeWorldDir = !config.googleDrive.makeWorldDir)));

        addRenderableWidget(new Button(this.width / 2 - 100, this.height / 6 + 168, 200, 20, CommonComponents.GUI_DONE, button -> popScreen()));
    }

    @Override
    public void popScreen() {
        ConfigHandler.saveConfig(config);
        CloudBackup.reloadConfig();
        super.popScreen();
    }

    public void refreshLoginStatusState() {
        if (googleDrive.isConnected()) {
            try {
                if (authUser == null) {
                    authUser = googleDrive.getDriveService().about().get().setFields("user").execute().getUser();
                    LOGGER.info("Drive User: " + authUser);
                }
                loginStatus = Component.translatable("cloudbackup.storage.google_drive.logged_as", authUser.getDisplayName(), authUser.getEmailAddress());
            } catch (IOException | GeneralSecurityException e) {
                LOGGER.error("Could not get user info", e);
            }
        } else {
            loginStatus = Component.translatable("cloudbackup.storage.not_connected");
        }
    }

    @Override
    public void render(@NotNull PoseStack arg, int i, int j, float f) {
        if (googleDrive.isConnected()) {
            connectButton.active = false;
            disconnectButton.active = true;
            uploadFolderEditBox.active = true;
            makeWorldFolderButton.active = true;
        } else {
            connectButton.active = true;
            disconnectButton.active = false;
            uploadFolderEditBox.active = false;
            makeWorldFolderButton.active = false;
        }
        super.render(arg, i, j, f);
        this.uploadFolderEditBox.render(arg, i, j , f);

        refreshLoginStatusState();
        if (loginStatus != null) {
            drawCenteredString(arg, font, loginStatus, this.width / 2, 47, -1);
        }
    }
}
