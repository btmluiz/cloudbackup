package dev.nardole.cloudbackup.client.gui.entries;

import com.google.common.collect.Lists;
import com.mojang.blaze3d.platform.Window;
import me.shedaniel.clothconfig2.gui.entries.TooltipListEntry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.network.chat.Component;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;

@Environment(EnvType.CLIENT)
public class ButtonListEntry extends TooltipListEntry<String> {
    private final Button buttonWidget;

    private final List<AbstractWidget> widgets;

    private final Supplier<Component> textSupplier;

    public ButtonListEntry(Component fieldName, Supplier<Component> textSupplier, Button.OnPress onPress, Supplier<Optional<Component[]>> tooltipSupplier, boolean requiresRestart) {
        super(fieldName, tooltipSupplier, requiresRestart);

        this.textSupplier = textSupplier;
        this.buttonWidget = Button.builder(Component.empty(), onPress).bounds(0, 0, 150, 20).build();

        this.widgets = Lists.newArrayList(new AbstractWidget[]{this.buttonWidget});
    }

    public void render(GuiGraphics graphics, int index, int y, int x, int entryWidth, int entryHeight, int mouseX, int mouseY, boolean isHovered, float delta) {
        super.render(graphics, index, y, x, entryWidth, entryHeight, mouseX, mouseY, isHovered, delta);
        Window window = Minecraft.getInstance().getWindow();

        this.buttonWidget.active = this.isEditable();
        this.buttonWidget.setY(y);
        this.buttonWidget.setMessage(this.textSupplier.get());

        Component displayedFieldName = this.getDisplayedFieldName();

        if (Minecraft.getInstance().font.isBidirectional()) {
            graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), window.getGuiScaledWidth() - x - Minecraft.getInstance().font.width(displayedFieldName), y + 6, 16777215);
            this.buttonWidget.setX(x);
        } else {
            graphics.drawString(Minecraft.getInstance().font, displayedFieldName.getVisualOrderText(), x, y + 6, this.getPreferredTextColor());
            this.buttonWidget.setX(x + entryWidth - 150);
        }

        this.buttonWidget.setWidth(150);
        this.buttonWidget.render(graphics, mouseX, mouseY, delta);
    }

    public @NotNull List<? extends GuiEventListener> children() {
        return this.widgets;
    }

    public List<? extends NarratableEntry> narratables() {
        return this.widgets;
    }

    @Override
    public String getValue() {
        return null;
    }

    @Override
    public Optional<String> getDefaultValue() {
        return Optional.empty();
    }
}
