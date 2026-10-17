
package com.rotpaddon.exampleaddon.client.gui;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rotpaddon.exampleaddon.form.AlcorFormRegistry;
import com.rotpaddon.exampleaddon.form.IAlcorFormHolder;
import com.rotpaddon.exampleaddon.init.InitItems;
import com.rotpaddon.exampleaddon.network.AlcorNetwork;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;

import java.util.List;
import java.util.Locale;

public class AlcorFormSelectionScreen extends Screen {

    // ========================================
    // UI SETTINGS
    // ========================================

    private static final int SLOT_SIZE = 38;
    private static final int SLOT_GAP = 8;

    // Colors
    private static final int BLACK = 0xFF080A12;
    private static final int DARK = 0xFF151923;
    private static final int INNER = 0xFF252735;

    private static final int BORDER = 0xFF777A83;
    private static final int PINK = 0xFFE17BAA;
    private static final int HOVER = 0xFFB3D8E5;
    private static final int LOCKED = 0xFF41434A;

    private final ResourceLocation standId;
    private final int resolveLevel;
    private final int selectedId;

    private final List<AlcorFormRegistry.FormOption> choices;

    // ========================================
    // OPEN SCREEN
    // ========================================

    public static void open() {

        Minecraft mc = Minecraft.getInstance();

        if (mc.player == null || mc.screen != null) {
            return;
        }

        IStandPower power =
                IStandPower.getPlayerStandPower(mc.player);

        if (power == null
                || !power.hasPower()
                || !power.isActive()
                || !(power.getStandManifestation()
                instanceof IAlcorFormHolder)) {
            return;
        }

        ResourceLocation stand =
                power.getType().getRegistryName();

        if (stand == null
                || AlcorFormRegistry.getForms(stand).isEmpty()) {
            return;
        }

        int current =
                ((IAlcorFormHolder)
                        power.getStandManifestation())
                        .alcorGetForm();

        mc.setScreen(new AlcorFormSelectionScreen(
                stand,
                power.getResolveLevel(),
                current
        ));
    }

    private AlcorFormSelectionScreen(
            ResourceLocation standId,
            int resolveLevel,
            int selectedId) {

        super(new StringTextComponent(
                "Alcor Form Selector"
        ));

        this.standId = standId;
        this.resolveLevel = resolveLevel;
        this.selectedId = selectedId;

        this.choices =
                AlcorFormRegistry.getForms(standId);
    }

    // ========================================
    // SLOT POSITIONS
    // ========================================

    private int getColumns() {

        int available =
                Math.max(1,
                        (width - 20 + SLOT_GAP)
                                / (SLOT_SIZE + SLOT_GAP));

        return Math.max(1,
                Math.min(choices.size(), available));
    }

    private int getRows() {

        int columns = getColumns();

        return (choices.size() + columns - 1)
                / columns;
    }

    private int getSlotX(int index) {

        int columns = getColumns();

        int totalWidth =
                columns * SLOT_SIZE
                        + (columns - 1) * SLOT_GAP;

        return (width - totalWidth) / 2
                + (index % columns)
                * (SLOT_SIZE + SLOT_GAP);
    }

    private int getSlotY(int index) {

        int rows = getRows();

        int totalHeight =
                rows * SLOT_SIZE
                        + (rows - 1) * SLOT_GAP;

        return (height - totalHeight) / 2
                + (index / getColumns())
                * (SLOT_SIZE + SLOT_GAP);
    }

    private boolean isUnlocked(
            AlcorFormRegistry.FormOption form) {

        return form.implemented
                && resolveLevel >= form.requiredResolve;
    }

    private boolean isInside(
            double mouseX,
            double mouseY,
            int index) {

        int x = getSlotX(index);
        int y = getSlotY(index);

        return mouseX >= x
                && mouseX < x + SLOT_SIZE
                && mouseY >= y
                && mouseY < y + SLOT_SIZE;
    }

    // ========================================
    // RENDER SCREEN
    // ========================================

    @Override
    public void render(
            MatrixStack stack,
            int mouseX,
            int mouseY,
            float partialTicks) {

        // No renderBackground():
        // Minecraft world stays visible behind UI.

        if (choices.isEmpty()) {
            return;
        }

        AlcorFormRegistry.FormOption hovered = null;
        AlcorFormRegistry.FormOption selected = null;

        for (int i = 0; i < choices.size(); i++) {

            AlcorFormRegistry.FormOption form =
                    choices.get(i);

            if (form.id == selectedId) {
                selected = form;
            }

            if (isInside(mouseX, mouseY, i)) {
                hovered = form;
            }

            drawSlot(
                    stack,
                    getSlotX(i),
                    getSlotY(i),
                    form,
                    form.id == selectedId,
                    isInside(mouseX, mouseY, i)
            );
        }

        // ====================================
        // TOP TITLE
        // ====================================

        AlcorFormRegistry.FormOption displayed =
                hovered != null ? hovered : selected;

        String formName = displayed != null
                ? displayed.name.toUpperCase(Locale.ROOT)
                : "SELECT FORM";

        int firstY = getSlotY(0);
        int lastY = getSlotY(choices.size() - 1)
                + SLOT_SIZE;

        String standName = standId.getPath()
                .replace('_', ' ')
                .toUpperCase(Locale.ROOT);

        drawCenteredString(
                stack,
                font,
                standName,
                width / 2,
                firstY - 38,
                0xB3D8E5
        );

        drawCenteredString(
                stack,
                font,
                formName,
                width / 2,
                firstY - 23,
                0xFFFFFF
        );

        // ====================================
        // BOTTOM TEXT
        // ====================================

        String bottomText = "SELECT FORM";

        if (hovered != null) {

            if (!hovered.implemented) {
                bottomText = "COMING SOON";
            }
            else if (!isUnlocked(hovered)) {
                bottomText = "LOCKED - RESOLVE "
                        + hovered.requiredResolve;
            }
        }

        drawCenteredString(
                stack,
                font,
                bottomText,
                width / 2,
                lastY + 15,
                0xFFFFFF
        );

        super.render(
                stack,
                mouseX,
                mouseY,
                partialTicks
        );
    }

    // ========================================
    // DRAW ONE SQUARE SLOT
    // ========================================

    private void drawSlot(
            MatrixStack stack,
            int x,
            int y,
            AlcorFormRegistry.FormOption form,
            boolean selected,
            boolean hovered) {

        boolean unlocked = isUnlocked(form);

        int frameColor = selected
                ? PINK
                : hovered && unlocked
                  ? HOVER
                  : BORDER;

        // Shadow
        fill(stack,
                x + 2, y + 3,
                x + SLOT_SIZE + 3,
                y + SLOT_SIZE + 4,
                0x99000000);

        // Black outer border
        fill(stack,
                x - 2, y - 2,
                x + SLOT_SIZE + 2,
                y + SLOT_SIZE + 2,
                BLACK);

        // Metal or selected pink border
        fill(stack,
                x, y,
                x + SLOT_SIZE,
                y + SLOT_SIZE,
                frameColor);

        // Dark inner border
        fill(stack,
                x + 3, y + 3,
                x + SLOT_SIZE - 3,
                y + SLOT_SIZE - 3,
                DARK);

        // Center
        fill(stack,
                x + 5, y + 5,
                x + SLOT_SIZE - 5,
                y + SLOT_SIZE - 5,
                INNER);

        // Small top reflection
        fill(stack,
                x + 6, y + 5,
                x + SLOT_SIZE - 6,
                y + 7,
                0xFF363B4C);

        // Selected pink accent
        if (selected) {

            fill(stack,
                    x + 5, y + SLOT_SIZE - 7,
                    x + SLOT_SIZE - 5,
                    y + SLOT_SIZE - 5,
                    PINK);
        }

        drawFormIcon(stack, x, y, form);

        // Darken unavailable forms
        if (!unlocked) {

            fill(stack,
                    x + 5, y + 5,
                    x + SLOT_SIZE - 5,
                    y + SLOT_SIZE - 5,
                    0xAA080A12);

            drawCenteredString(
                    stack,
                    font,
                    "x",
                    x + SLOT_SIZE / 2,
                    y + SLOT_SIZE - 13,
                    LOCKED
            );
        }
    }

    // ========================================
    // FORM ICONS
    // ========================================

    private void drawFormIcon(
            MatrixStack stack,
            int x,
            int y,
            AlcorFormRegistry.FormOption form) {

        int centerX = x + SLOT_SIZE / 2;
        int centerY = y + SLOT_SIZE / 2;

        if (AlcorFormRegistry.BLUE_NIGHT.equals(standId)) {

            // Bracelet icon
            if (form.id == 0) {

                // Outer silver band
                fill(stack,
                        centerX - 10, centerY - 7,
                        centerX + 10, centerY - 4,
                        0xFFB3D8E5);

                fill(stack,
                        centerX - 10, centerY + 4,
                        centerX + 10, centerY + 7,
                        0xFFB3D8E5);

                fill(stack,
                        centerX - 10, centerY - 4,
                        centerX - 7, centerY + 4,
                        0xFF19233B);

                fill(stack,
                        centerX + 7, centerY - 4,
                        centerX + 10, centerY + 4,
                        0xFF19233B);

                // Moonstone center
                fill(stack,
                        centerX - 3, centerY - 5,
                        centerX + 3, centerY + 5,
                        0xFFB3D8E5);

                fill(stack,
                        centerX - 1, centerY - 3,
                        centerX + 1, centerY + 3,
                        0xFF19233B);

                return;
            }

            // Moon Reaper — render the actual item
            if (form.id == 1) {

                ItemStack scythe = new ItemStack(
                        InitItems.BLUE_NIGHT_SCYTHE.get()
                );

                minecraft.getItemRenderer().renderGuiItem(
                        scythe,
                        centerX - 8,
                        centerY - 8
                );

                return;
            }
        }

        // Generic icon for other forms.
        // Works for Blue Night and future Stands.
        String symbol;

        if (form.name.toUpperCase(Locale.ROOT)
                .startsWith("ACT ")) {

            symbol = form.name.substring(4);
        }
        else {
            symbol = form.name.substring(0, 1)
                    .toUpperCase(Locale.ROOT);
        }

        drawCenteredString(
                stack,
                font,
                symbol,
                centerX,
                centerY - 4,
                0xB3D8E5
        );
    }

    // ========================================
    // CLICK TO SELECT FORM
    // ========================================

    @Override
    public boolean mouseClicked(
            double mouseX,
            double mouseY,
            int button) {

        if (button == 0) {

            for (int i = 0; i < choices.size(); i++) {

                if (!isInside(mouseX, mouseY, i)) {
                    continue;
                }

                AlcorFormRegistry.FormOption form =
                        choices.get(i);

                if (!isUnlocked(form)) {
                    return true;
                }

                // Existing multiplayer system
                AlcorNetwork.sendToServer(form.id);

                // Close after selection
                minecraft.setScreen(null);

                return true;
            }
        }

        return super.mouseClicked(
                mouseX,
                mouseY,
                button
        );
    }

    // ========================================
    // KEEP THE WORLD RUNNING
    // ========================================

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
