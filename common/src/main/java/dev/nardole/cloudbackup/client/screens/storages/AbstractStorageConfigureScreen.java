package dev.nardole.cloudbackup.client.screens.storages;

import dev.nardole.cloudbackup.client.screens.AbstractCloudScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class AbstractStorageConfigureScreen extends AbstractCloudScreen {

    public AbstractStorageConfigureScreen(Component title, Screen lastScreen) {
        super(title, lastScreen);
    }
}
