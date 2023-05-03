package dev.nardole.cloudbackup.mixin.client.gui.screens;

import dev.nardole.cloudbackup.client.screens.CloudScreen;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.OptionsScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(OptionsScreen.class)
public abstract class OptionsScreenMixin extends Screen {
    private static final Component CLOUD_BACKUP = new TranslatableComponent("titles_screen.buttons.cloud");
    protected OptionsScreenMixin(Component component) {
        super(component);
    }

    @Inject(method = "init", at = @At("RETURN"))
    protected void addCustomButton(CallbackInfo ci) {
        int k = this.height / 6;
        int j = this.height / 7;
        int i = j - k;

        for (AbstractWidget button : this.buttons) {
            button.y -= i;
        }

        this.addButton(new Button(this.width / 2 - 155, this.height / 6 + 144 - 6 - i, 150, 20, CLOUD_BACKUP, button -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new CloudScreen(this));
        }));
    }
}
