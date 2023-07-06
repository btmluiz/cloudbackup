package dev.nardole.cloudbackup.client.gui.builders;

import dev.nardole.cloudbackup.client.gui.entries.ButtonListEntry;
import me.shedaniel.clothconfig2.impl.builders.AbstractFieldBuilder;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;
import java.util.function.Supplier;

public class ButtonBuilder extends AbstractFieldBuilder<String, ButtonListEntry, ButtonBuilder> {
    private Button.OnPress onPress;

    private Supplier<Component> textSupplier;

    public ButtonBuilder(Component fieldNameKey) {
        super(Component.empty(), fieldNameKey);
    }

    public ButtonBuilder setOnClick(Button.OnPress onPress) {
        this.onPress = onPress;
        return this;
    }

    public ButtonBuilder setButtonText(Supplier<Component> textSupplier) {
        this.textSupplier = textSupplier;
        return this;
    }

    @Override
    public @NotNull ButtonListEntry build() {
        ButtonListEntry entry = new ButtonListEntry(this.getFieldNameKey(), this.textSupplier, this.onPress, null, false);

        entry.setTooltipSupplier(() -> this.getTooltipSupplier().apply(entry.getValue()));

        return entry;
    }
}
