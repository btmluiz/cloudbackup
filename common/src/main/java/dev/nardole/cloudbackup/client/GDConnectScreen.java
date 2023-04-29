package dev.nardole.cloudbackup.client;

import dev.nardole.cloudbackup.storages.GoogleDriveStorage;
import net.minecraft.Util;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GDConnectScreen extends AbstractCloudScreen {

    public static final GoogleDriveStorage GOOGLE_DRIVE = new GoogleDriveStorage();
    public static Component TITLE = new TranslatableComponent("gd_screen.title");
    public static final Logger LOGGER = LogManager.getLogger();

    public GDConnectScreen(Screen lastScreen) {
        super(TITLE, lastScreen);
    }

    protected void init() {
//        this.mainContent = new GridLayout();
//        GridLayout.RowHelper mainContent$rowHelper = this.mainContent.createRowHelper(1);
//
//        Button openBrowser = mainContent$rowHelper.addChild(Button.builder(Component.translatable("open_browser"), (button -> {
//            try {
//                if (GOOGLE_DRIVE.getHasError()) {
//                    LOGGER.error("Cannot continue error detected");
//                } else {
//
//                    String driveUrl = GOOGLE_DRIVE.getAuthorizationUrl();
//
//                    Util.getPlatform().openUri(driveUrl);
//                    this.minecraft.setScreen(this);
//                }
//            } catch (Exception e){
//                LOGGER.error("Could not get authorization url");
//            }
//        })).build());

        this.addButton(new Button(this.width / 2 - 155, this.height/6 + 48 - 6, 150, 20, new TranslatableComponent("cloud_locations.open_browser"), (button -> {
            try {
                if (GOOGLE_DRIVE.getHasError()) {
                    LOGGER.error("Cannot continue error detected");
                } else {

                    String driveUrl = GOOGLE_DRIVE.getAuthorizationUrl();

                    Util.getPlatform().openUri(driveUrl);
                    this.minecraft.setScreen(this);
                }
            } catch (Exception e){
                LOGGER.error("Could not get authorization url");
            }
        })));

//        this.mainContent.visitWidgets((consumer) -> {
//            consumer.setTabOrderGroup(1);
//            this.addRenderableWidget(consumer);
//        });
//
//        this.repositionElements();
    }
}
