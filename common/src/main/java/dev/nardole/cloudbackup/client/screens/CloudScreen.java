package dev.nardole.cloudbackup.client.screens;

import com.mojang.blaze3d.vertex.PoseStack;
import dev.nardole.cloudbackup.CloudBackup;
import dev.nardole.cloudbackup.client.screens.lists.ConfigList;
import dev.nardole.cloudbackup.config.ConfigHandler;
import dev.nardole.cloudbackup.config.MainConfig;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;

public class CloudScreen extends Screen {

    public static Component TITLE = new TranslatableComponent("cloudbackup.title");

    private ConfigList configList;

    private final MainConfig mainConfig = CloudBackup.getConfig().clone();

    private final Screen lastScreen;

    public CloudScreen(Screen lastScreen) {
        super(TITLE);
        this.lastScreen = lastScreen;
    }

    protected void init() {
        this.configList = new ConfigList(this, this.minecraft, mainConfig);
        this.children.add(this.configList);

        this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, new TranslatableComponent("cloudbackup.save"), button -> {
            ConfigHandler.saveConfig(mainConfig);
            CloudBackup.reloadConfig();
            assert this.minecraft != null;
            this.minecraft.setScreen(this.lastScreen);
        }));

        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, CommonComponents.GUI_CANCEL, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(this.lastScreen);
        }));
    }

    public void render(PoseStack poseStack, int i, int j, float f) {
        this.renderBackground(poseStack);
        this.configList.render(poseStack, i, j, f);
        drawCenteredString(poseStack, this.font, this.title, this.width / 2, 20, -1);

        super.render(poseStack, i, j, f);
    }
}
