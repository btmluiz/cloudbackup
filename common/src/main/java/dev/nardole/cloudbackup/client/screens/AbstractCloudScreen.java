package dev.nardole.cloudbackup.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractCloudScreen extends Screen {

    private final Screen lastScreen;

    public AbstractCloudScreen(Component title, Screen lastScreen) {
        super(title);

        this.lastScreen = lastScreen;
    }


    @Override
    public void render(@NotNull PoseStack arg, int i, int j, float f) {
        this.renderBackground(arg);
        drawCenteredString(arg, this.font, this.title, this.width / 2, 20, -1);
        super.render(arg, i, j, f);
    }

    public void popScreen() {
        if (this.minecraft != null) {
            this.minecraft.setScreen(this.lastScreen);
        }
    }

    @Override
    public void onClose() {
        this.popScreen();
    }
}
