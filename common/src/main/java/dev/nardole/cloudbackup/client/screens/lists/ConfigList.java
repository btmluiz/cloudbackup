package dev.nardole.cloudbackup.client.screens.lists;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.vertex.PoseStack;
import dev.nardole.cloudbackup.client.screens.CloudScreen;
import dev.nardole.cloudbackup.client.screens.storages.StorageScreenFactory;
import dev.nardole.cloudbackup.config.MainConfig;
import dev.nardole.cloudbackup.storages.CloudStorage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ContainerObjectSelectionList;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;

import java.util.Collections;
import java.util.List;

import static dev.nardole.cloudbackup.CloudBackup.LOGGER;

public class ConfigList extends ContainerObjectSelectionList<ConfigList.Entry> {

    public ConfigList(CloudScreen cloudScreen, Minecraft minecraft, MainConfig mainConfig) {
        super(minecraft, cloudScreen.width, cloudScreen.height, 43, cloudScreen.height - 32, 25);
//        this.setRenderBackground(false);

        int cellWidth = getCellWidth();

        Button enableBackupButton = new Button(0, 0, cellWidth, 20, TextComponent.EMPTY, button -> mainConfig.enableBackup = !mainConfig.enableBackup) {
            @Override
            public Component getMessage() {
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloudbackup.enable_backup"), new TranslatableComponent(mainConfig.enableBackup ? "options.on" : "options.off"));
            }
        };

        Button autoBackupButton = new Button(0, 0, cellWidth, 20, TextComponent.EMPTY, button -> mainConfig.autoBackup = !mainConfig.autoBackup) {
            @Override
            public Component getMessage() {
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloudbackup.auto_backup"), new TranslatableComponent(mainConfig.autoBackup ? "options.on" : "options.off"));
            }
        };

        Button backupWhenExitButton = new Button(0, 0, cellWidth, 20, TextComponent.EMPTY, button -> mainConfig.backupWhenExit = !mainConfig.backupWhenExit) {
            @Override
            public Component getMessage() {
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloudbackup.backup_when_exit"), new TranslatableComponent(mainConfig.backupWhenExit ? "options.on" : "options.off"));
            }
        };

        Button broadCastBackupMessageButton = new Button(0, 0, cellWidth, 20, TextComponent.EMPTY, button -> mainConfig.broadCastBackupMessage = !mainConfig.broadCastBackupMessage) {
            @Override
            public Component getMessage() {
                return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloudbackup.broadCastBackupMessage"), new TranslatableComponent(mainConfig.broadCastBackupMessage ? "options.on" : "options.off"));
            }
        };

        this.addEntry(new ButtonRowEntry(enableBackupButton, autoBackupButton));
        this.addEntry(new ButtonRowEntry(backupWhenExitButton, broadCastBackupMessageButton));

        try {
            for (CloudStorage storage : CloudStorage.values()) {
                this.addEntry(new CategoryEntry(storage.getDisplayName()));
                this.addEntry(new StorageEntry(storage, mainConfig.getStorageConfig(storage), StorageScreenFactory.invoke(storage, cloudScreen)));
            }
        } catch (Exception e) {
            LOGGER.error("Error while creating storage config screen", e);
        }
    }

    private int getCellWidth() {
        return 150;
    }

    protected int getScrollbarPosition() {
        return super.getScrollbarPosition() + 55;
    }

    public int getRowWidth() {
        return 310;
    }

    public class ButtonRowEntry extends Entry {

        protected Button button1;
        protected Button button2;

        public ButtonRowEntry() {
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return ImmutableList.of(this.button1, this.button2);
        }

        public ButtonRowEntry(Button button1, Button button2) {
            this.button1 = button1;
            this.button2 = button2;
        }

        @Override
        public void render(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            ConfigList.this.minecraft.font.getClass();

            this.button1.x = k;
            this.button1.y = j;
            this.button1.render(poseStack, n, o, f);

            this.button2.x = k + 160;
            this.button2.y = j;
            this.button2.render(poseStack, n, o, f);
        }

        @Override
        public List<? extends GuiEventListener> children() {
            return ImmutableList.of(this.button1, this.button2);
        }

        @Override
        public boolean mouseClicked(double d, double e, int i) {
            return this.button1.mouseClicked(d, e, i) || this.button2.mouseClicked(d, e, i);
        }

        @Override
        public boolean mouseReleased(double d, double e, int i) {
            return this.button1.mouseReleased(d, e, i) || this.button2.mouseReleased(d, e, i);
        }
    }

    public class StorageEntry extends ButtonRowEntry {
        private StorageEntry(final CloudStorage storage, MainConfig.StorageConfig config, Screen configScreen) {
            super();

            int cellWidth = getCellWidth();

            this.button1 = new Button(0, 0, cellWidth, 20, TextComponent.EMPTY, button -> {
                LOGGER.info(storage.getName() + " enableButton: " + (config.enabled ? "on" : "off"));
                config.enabled = !config.enabled;
            }) {
                @Override
                public Component getMessage() {
                    return new TranslatableComponent("options.generic_value", new TranslatableComponent("cloudbackup.upload"), new TranslatableComponent("options." + (config.enabled ? "on" : "off")));
                }
            };

            this.button2 = new Button(0, 0, cellWidth, 20, new TranslatableComponent("cloudbackup.configure"), button -> minecraft.setScreen(configScreen));
        }
    }

    public class CategoryEntry extends Entry {
        private final Component name;

        private final int width;

        public CategoryEntry(Component component) {
            this.name = component;
            this.width = ConfigList.this.minecraft.font.width(this.name);
        }

        public void render(PoseStack poseStack, int i, int j, int k, int l, int m, int n, int o, boolean bl, float f) {
            Font font = ConfigList.this.minecraft.font;
            Component component = this.name;

            assert ConfigList.this.minecraft.screen != null;
            float width = (float) (ConfigList.this.minecraft.screen.width / 2 - this.width / 2);

            int height = j + m;
            ConfigList.this.minecraft.font.getClass();
            font.draw(poseStack, component, width, (float) (height - 9 - 1), 16777215);
        }

        public boolean changeFocus(boolean bl) {
            return false;
        }

        public List<? extends GuiEventListener> children() {
            return Collections.emptyList();
        }

        @Override
        public List<? extends NarratableEntry> narratables() {
            return Collections.emptyList();
        }
    }

    public abstract static class Entry extends ContainerObjectSelectionList.Entry<Entry> {
        public Entry() {
        }
    }
}
