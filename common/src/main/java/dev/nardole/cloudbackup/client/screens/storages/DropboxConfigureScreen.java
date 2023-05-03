package dev.nardole.cloudbackup.client.screens.storages;

import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class DropboxConfigureScreen extends AbstractStorageConfigureScreen {
    private static final Component TITLE = null;

    public DropboxConfigureScreen(Screen lastScreen) {
        super(TITLE, lastScreen);
    }
}
