package dev.nardole.cloudbackup.mixin.client;


import dev.nardole.cloudbackup.client.CloudScreen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TitleScreen.class)
public abstract class TitleScreenMixin extends Screen {

    private static final Component CLOUD_BACKUP = new TranslatableComponent("titles_screen.buttons.cloud");
    private static final Logger LOGGER = LogManager.getLogger();

    protected TitleScreenMixin(Component title) {
        super(title);
    }

    @Inject(at = @At(value = "RETURN"), method = "init()V", locals = LocalCapture.CAPTURE_FAILHARD)
    private void addCustomButton(CallbackInfo ci, int i, int j) {
        assert this.minecraft != null;
        if (!this.minecraft.isDemo()) {
            this.addButton(new Button(this.width / 2 - 100 + 205, i, 98, 20, CLOUD_BACKUP, (button) -> {
                this.minecraft.setScreen(new CloudScreen(this));
            }));
        }
    }
}
