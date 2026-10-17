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

import org.lwjgl.glfw.GLFW;

import java.util.List;
import java.util.Locale;

/**
 * Reusable, world-visible ACT-style HUD. A Stand provides its available forms
 * through AlcorFormRegistry; the server still validates every requested form.
 *
 * No standard Minecraft buttons or background are used: the form tiles are
 * rendered directly so the selector can also serve Fortunate Son later.
 */
public class AlcorFormSelectionScreen extends Screen {
    private static final int PINK = 0xFFF77BD5;
    private static final int CYAN = 0xFFB3D8E5;
    private static final int WHITE = 0xFFF4F3FB;
    private static final int MUTED = 0xFF94A1BA;

    private final ResourceLocation standId;
    private final int resolveLevel;
    private final int selectedId;
    private final List<AlcorFormRegistry.FormOption> forms;

    private int tileSize;
    private int gap;
    private int columns;
    private int startY;
    private int focusedIndex = 0;

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

    private AlcorFormSelectionScreen(ResourceLocation standId, int resolveLevel, int selectedId) {
        super(new StringTextComponent("Alcor Form Selector"));
        this.standId = standId;
        this.resolveLevel = resolveLevel;
        this.selectedId = selectedId;
        this.forms = AlcorFormRegistry.getForms(standId);
    }

    @Override
    protected void init() {
        super.init();
        boolean compact = this.height < 220;
        this.tileSize = compact ? 46 : 56;
        this.gap = compact ? 8 : 12;

        // ACT-style four-in-a-row layout; six Blue Night forms use two
        // centered rows of three. Shrink column count for narrow windows.
        int desiredColumns = forms.size() > 4 ? 3 : Math.max(1, forms.size());
        int fittingColumns = Math.max(1, (this.width - 24 + gap) / (tileSize + gap));
        this.columns = Math.min(desiredColumns, fittingColumns);

        int rows = (forms.size() + columns - 1) / columns;
        int gridHeight = rows * tileSize + (rows - 1) * gap;
        this.startY = this.height / 2 - gridHeight / 2;

        for (int i = 0; i < forms.size(); i++) {
            if (forms.get(i).id == selectedId) {
                this.focusedIndex = i;
                break;
            }
        }
    }

    private boolean isUnlocked(AlcorFormRegistry.FormOption form) {
        return form.implemented && resolveLevel >= form.requiredResolve;
    }

    private int tileX(int index) {
        int row = index / columns;
        int col = index % columns;
        int inRow = Math.min(columns, forms.size() - row * columns);
        int rowWidth = inRow * tileSize + (inRow - 1) * gap;
        return (this.width - rowWidth) / 2 + col * (tileSize + gap);
    }

    private int tileY(int index) {
        return startY + (index / columns) * (tileSize + gap);
    }

    private int tileAt(double mouseX, double mouseY) {
        for (int i = 0; i < forms.size(); i++) {
            int x = tileX(i);
            int y = tileY(i);
            if (mouseX >= x && mouseX < x + tileSize &&
                    mouseY >= y && mouseY < y + tileSize) return i;
        }
        return -1;
    }

    private boolean isActSelector() {
        return !forms.isEmpty() &&
                forms.get(0).name.toUpperCase(Locale.ROOT).startsWith("ACT");
    }

    private String displayTitle(AlcorFormRegistry.FormOption form) {
        if (AlcorFormRegistry.BLUE_NIGHT.equals(standId) && form.id == 1) {
            return "MOON REAPER";
        }
        return form.name.toUpperCase(Locale.ROOT);
    }

    private void renderBracelet(MatrixStack stack, int centerX, int centerY, boolean enabled) {
        int outline = enabled ? CYAN : 0xFF586075;
        int shine = enabled ? PINK : 0xFF616778;
        // A simple pixel-art ring, so the bracelet has its own icon without
        // requiring a dummy inventory item or a new texture.
        fill(stack, centerX - 10, centerY - 5, centerX - 7, centerY + 5, outline);
        fill(stack, centerX + 7, centerY - 5, centerX + 10, centerY + 5, outline);
        fill(stack, centerX - 7, centerY - 8, centerX + 7, centerY - 5, shine);
        fill(stack, centerX - 7, centerY + 5, centerX + 7, centerY + 8, outline);
        fill(stack, centerX - 2, centerY - 10, centerX + 2, centerY - 7, WHITE);
    }

    private void renderTile(MatrixStack stack, int index, boolean hovered) {
        AlcorFormRegistry.FormOption form = forms.get(index);
        boolean unlocked = isUnlocked(form);
        boolean selected = form.id == selectedId;
        boolean focused = index == focusedIndex;
        int x = tileX(index);
        int y = tileY(index);

        // Selected tile is hot pink, hovered tile is cyan, locked tiles grey.
        int border = selected ? PINK : hovered || focused ? CYAN : 0xFF536078;
        if (!unlocked) border = hovered || focused ? 0xFF8390A8 : 0xFF3B4557;
        fill(stack, x - 2, y - 2, x + tileSize + 2, y + tileSize + 2, border);
        fill(stack, x, y, x + tileSize, y + tileSize,
                unlocked ? 0xB0121A30 : 0xB00C101B);
        fill(stack, x + 3, y + 3, x + tileSize - 3, y + 5,
                selected ? PINK : unlocked ? CYAN : 0xFF414B61);

        int iconX = x + tileSize / 2;
        int iconY = y + tileSize / 2 - 6;

        if (AlcorFormRegistry.BLUE_NIGHT.equals(standId) && form.id == 0) {
            renderBracelet(stack, iconX, iconY, unlocked);
        }
        else if (AlcorFormRegistry.BLUE_NIGHT.equals(standId) && form.id == 1) {
            // Preview the actual working Moon Reaper item in the tile.
            this.minecraft.getItemRenderer().renderAndDecorateItem(
                    new ItemStack(InitItems.BLUE_NIGHT_SCYTHE.get()), iconX - 8, iconY - 8);
        }
        else {
            String iconText = form.name.toUpperCase(Locale.ROOT).startsWith("ACT")
                    ? form.name.substring(3).trim()
                    : form.name.substring(0, 1).toUpperCase(Locale.ROOT);
            drawCenteredString(stack, this.font, iconText, iconX, iconY - 4,
                    unlocked ? WHITE : 0xFF637087);
        }

        // The small label identifies future forms without needing textures yet.
        stack.pushPose();
        stack.translate(iconX, y + tileSize - 12, 0);
        stack.scale(0.70F, 0.70F, 1.0F);
        drawCenteredString(stack, this.font, form.name.toUpperCase(Locale.ROOT), 0, 0,
                unlocked ? WHITE : 0xFF768198);
        stack.popPose();
    }

    @Override
    public void render(MatrixStack stack, int mouseX, int mouseY, float partialTicks) {
        // Intentionally no renderBackground(): the Minecraft world stays visible.
        fill(stack, 0, 0, this.width, this.height, 0x30060B1A);

        int hovered = tileAt(mouseX, mouseY);
        int presented = hovered >= 0 ? hovered : focusedIndex;
        if (presented < 0 || presented >= forms.size()) presented = 0;

        if (!forms.isEmpty()) {
            AlcorFormRegistry.FormOption active = forms.get(presented);
            int titleY = Math.max(3, startY - (this.height < 220 ? 31 : 40));

            stack.pushPose();
            stack.translate(this.width / 2.0F, titleY, 0);
            stack.scale(1.45F, 1.45F, 1.0F);
            drawCenteredString(stack, this.font, displayTitle(active), 0, 0,
                    isUnlocked(active) ? PINK : MUTED);
            stack.popPose();

            drawCenteredString(stack, this.font,
                    standId.getPath().replace('_', ' ').toUpperCase(Locale.ROOT),
                    this.width / 2, Math.max(13, startY - 16), CYAN);
        }

        for (int i = 0; i < forms.size(); i++) {
            renderTile(stack, i, i == hovered);
        }

        int rows = (forms.size() + columns - 1) / columns;
        int bottom = startY + rows * tileSize + (rows - 1) * gap;
        drawCenteredString(stack, this.font,
                isActSelector() ? "Select Act" : "Select Form",
                this.width / 2, Math.min(this.height - 23, bottom + 12), WHITE);

        if (!forms.isEmpty()) {
            AlcorFormRegistry.FormOption active = forms.get(presented);
            String hint = !active.implemented ? "COMING SOON"
                    : !isUnlocked(active) ? "REQUIRES RESOLVE " + active.requiredResolve
                    : "CLICK OR ENTER TO SELECT  |  ESC TO CLOSE";
            drawCenteredString(stack, this.font, hint,
                    this.width / 2, Math.min(this.height - 11, bottom + 26),
                    isUnlocked(active) ? MUTED : PINK);
        }

        super.render(stack, mouseX, mouseY, partialTicks);
    }

    private void choose(int index) {
        if (index < 0 || index >= forms.size()) return;
        AlcorFormRegistry.FormOption form = forms.get(index);
        if (!isUnlocked(form)) return;
        AlcorNetwork.sendToServer(form.id);
        this.minecraft.setScreen(null);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        int index = tileAt(mouseX, mouseY);
        if (button == 0 && index >= 0) {
            focusedIndex = index;
            choose(index);
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_LEFT || keyCode == GLFW.GLFW_KEY_UP) {
            focusedIndex = (focusedIndex - 1 + forms.size()) % forms.size();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_RIGHT || keyCode == GLFW.GLFW_KEY_DOWN) {
            focusedIndex = (focusedIndex + 1) % forms.size();
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_ENTER || keyCode == GLFW.GLFW_KEY_KP_ENTER ||
                keyCode == GLFW.GLFW_KEY_SPACE) {
            choose(focusedIndex);
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean isPauseScreen() {
        return false;
    }
}
