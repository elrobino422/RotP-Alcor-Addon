package com.rotpaddon.exampleaddon.client.gui;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rotpaddon.exampleaddon.form.AlcorFormRegistry;
import com.rotpaddon.exampleaddon.form.IAlcorFormHolder;
import com.rotpaddon.exampleaddon.network.AlcorNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Locale;

/** Shared Tusk-inspired selection screen; forms come from the Stand's registry ID. */
public class AlcorFormSelectionScreen extends Screen {
    private final ResourceLocation standId;
    private final int resolveLevel;
    private final int selectedId;

    public static void open() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.screen != null) return;

        IStandPower power = IStandPower.getPlayerStandPower(mc.player);
        if (power == null || !power.hasPower() || !power.isActive() ||
                !(power.getStandManifestation() instanceof IAlcorFormHolder)) return;

        ResourceLocation stand = power.getType().getRegistryName();
        if (stand == null || AlcorFormRegistry.getForms(stand).isEmpty()) return;

        int current = ((IAlcorFormHolder) power.getStandManifestation()).alcorGetForm();
        mc.setScreen(new AlcorFormSelectionScreen(stand, power.getResolveLevel(), current));
    }

    private AlcorFormSelectionScreen(ResourceLocation standId,
                                     int resolveLevel, int selectedId) {
        super(new StringTextComponent("ALCOR FORM SELECTOR"));
        this.standId = standId;
        this.resolveLevel = resolveLevel;
        this.selectedId = selectedId;
    }

    @Override
    protected void init() {
        super.init();
        List<AlcorFormRegistry.FormOption> choices = AlcorFormRegistry.getForms(standId);
        int startX = this.width / 2 - 113;
        int startY = this.height / 2 - 51;

        for (int i = 0; i < choices.size(); i++) {
            final AlcorFormRegistry.FormOption form = choices.get(i);
            int x = startX + (i % 2) * 118;
            int y = startY + (i / 2) * 27;
            boolean unlocked = form.implemented && resolveLevel >= form.requiredResolve;
            String name = form.name;
            if (form.id == selectedId) name = "[" + name + "]";
            if (!form.implemented) name += " - Soon";
            else if (!unlocked) name += " - Locked";
            Button button = new Button(x, y, 108, 22,
                    new StringTextComponent(name), b -> {
                AlcorNetwork.sendToServer(form.id);
                this.minecraft.setScreen(null);
            });
            button.active = unlocked;
            this.addButton(button);
        }
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        this.renderBackground(stack);
        int x = this.width / 2;
        int y = this.height / 2;
        fill(stack, x - 133, y - 90, x + 133, y + 90, 0xE00B1527);
        fill(stack, x - 133, y - 90, x + 133, y - 87, 0xFFB3D8E5);
        fill(stack, x - 133, y + 87, x + 133, y + 90, 0xFF19233B);
        drawCenteredString(stack, this.font,
                standId.getPath().replace('_', ' ').toUpperCase(Locale.ROOT) + " / FORMS",
                x, y - 76, 0xB3D8E5);
        drawCenteredString(stack, this.font, "Choose your Stand form", x, y - 63, 0xAFC0D1);
        super.render(stack, mouseX, mouseY, partialTicks);
        drawCenteredString(stack, this.font, "Click a form | Esc to close", x, y + 69, 0x8496AF);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
