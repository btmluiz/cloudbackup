package dev.nardole.cloudbackup.client;

import dev.nardole.cloudbackup.storages.CloudStorage;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CloudScreen extends AbstractCloudScreen {

    public static Component TITLE = new TranslatableComponent("cloudbackup.title");

    public static final Logger LOGGER = LogManager.getLogger();

    private Button storageOption;

    private Button connectionButton;

    private CloudStorage storageSelected = CloudStorage.GOOGLE_DRIVE;

    public CloudScreen(Screen lastScreen) {
        super(TITLE, lastScreen);
    }

    protected void init() {
//        this.mainContent = new GridLayout();
//        this.mainContent.rowSpacing(8);
//
//        this.mainContent.defaultCellSetting().paddingHorizontal(5).paddingBottom(4).alignHorizontallyCenter();
//        GridLayout.RowHelper mainContent$rowHelper = this.mainContent.createRowHelper(2);
//        LayoutSettings layoutSettings = mainContent$rowHelper.newCellSettings();

        int i = this.width / 2 - 155;
        int j = this.width / 2 + 5;

        this.storageOption = (Button) this.addButton(new Button(i, 100, 150, 20, TextComponent.EMPTY, (arg) -> {
            switch (this.storageSelected) {
                case GOOGLE_DRIVE:
                    this.setStorageSelected(CloudStorage.DROPBOX);
                    break;
                case DROPBOX:
                    this.setStorageSelected(CloudStorage.GOOGLE_DRIVE);
                    break;
            }
        }) {
            public Component getMessage() {
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloud_screen.location"), CloudScreen.this.storageSelected.getDisplayName());
            }
        });

//        CycleButton<CloudLocation> cloudLocation = mainContent$rowHelper.addChild(CycleButton.builder(CloudLocation::getDisplayName).withValues(CloudLocation.values()).create(0, 0, 180, 20, Component.translatable("cloud_screen.location"), (button, location) -> {
//
//        }), layoutSettings);

        this.connectionButton = this.addButton(
                new Button(j, 100, 150, 20, new TranslatableComponent("cloudbackup.buttons.connection"), button -> {
                    this.minecraft.setScreen(
                            new GDConnectScreen(this)
                    );
                })
        );

//        this.mainContent.visitWidgets((consumer) -> {
//            consumer.setTabOrderGroup(1);
//            this.addRenderableWidget(consumer);
//        });
//
//        this.bottomButtons = (new GridLayout()).columnSpacing(10);
//        GridLayout.RowHelper gridLayout$rowhelper = this.bottomButtons.createRowHelper(2);
//        gridLayout$rowhelper.addChild(Button.builder(
//                CommonComponents.GUI_CANCEL, (button) -> {
//                    this.popScreen();
//                }
//        ).build());
//
//        this.bottomButtons.visitWidgets((consumer) -> {
//            consumer.setTabOrderGroup(1);
//            this.addRenderableWidget(consumer);
//        });
//
//        this.repositionElements();
    }

    public void setStorageSelected(CloudStorage storageSelected) {
        this.storageSelected = storageSelected;
    }
}
