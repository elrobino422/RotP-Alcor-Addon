package com.rotpaddon.exampleaddon.client.gui;

import com.github.standobyte.jojo.client.ClientModSettings;
import com.github.standobyte.jojo.client.ui.actionshud.ActionsOverlayGui.PositionConfig;
import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.mojang.blaze3d.matrix.MatrixStack;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.form.BlueNightInsomnia;
import com.rotpaddon.exampleaddon.init.InitStands;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Compact Insomnia bar displayed next to RotP's own stamina/resolve bars.
 *
 * Its anchor comes DIRECTLY from the same barsPosition setting used by RotP.
 * Switching Bars position in RotP's HUD settings also moves this bar.
 *
 * RotP does not publish an external 'register another bar' API, so an addon
 * overlay follows RotP's public config instead of modifying RotP classes.
 */
@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID, value = Dist.CLIENT,
        bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlueNightInsomniaHud {
    // Match the sizes used by RotP's BarsRenderer in Minecraft 1.16.5.
    private static final int ROT_P_BARS_WIDTH = 28;
    private static final int ROT_P_BARS_HEIGHT = 100;
    private static final int ROT_P_HORIZONTAL_LENGTH = 200;
    private static final int ROT_P_ICON_WIDTH = 12;
    private static final int INDENT = 4;
    private static final int GAP = 4;

    private static final int BAR_OUTLINE = 0xFF14172A;
    private static final int BAR_BORDER = 0xFF536C92;
    private static final int BAR_EMPTY = 0xFF10182B;
    private static final int BAR_BLUE = 0xFF3465B6;
    private static final int BAR_CYAN = 0xFF59B8E3;
    private static final int BAR_ICE = 0xFFB3E9FF;
    private static final int LABEL_COLOR = 0xFFB3D8E5;

    private static int serverStacks;
    private static float animatedStacks;

    private BlueNightInsomniaHud() {}

    /**
     * HUD's last server-synced resource amount. Used for client-side
     * skill availability; the server still validates and pays the cost.
     */
    public static boolean hasAtLeast(int amount) {
        return serverStacks >= amount;
    }

    /** Client-side cache, updated only from the server's Insomnia packet. */
    public static void receive(int stacks) {
        serverStacks = Math.max(0, Math.min(BlueNightInsomnia.MAX_STACKS, stacks));
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        clear();
    }

    @SubscribeEvent
    public static void onRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        clear();
    }

    private static void clear() {
        serverStacks = 0;
        animatedStacks = 0;
    }

    @SubscribeEvent
    public static void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null
                || mc.options.hideGui) return;

        boolean blueNightActive = IStandPower.getStandPowerOptional(mc.player)
                .map(power -> power.isActive()
                        && power.getType() == InitStands.STAND_BLUE_NIGHT.getStandType())
                .orElse(false);
        if (!blueNightActive) return;

        // This is the same setting modified by the player's RotP HUD button:
        // TOP_LEFT, TOP_RIGHT, LEFT or RIGHT.
        PositionConfig bars = ClientModSettings.getSettingsReadOnly().barsPosition;
        if (bars == null) return;

        animatedStacks += (serverStacks - animatedStacks) * 0.16F;
        if (Math.abs(animatedStacks - serverStacks) < 0.05F) {
            animatedStacks = serverStacks;
        }

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();
        MatrixStack stack = event.getMatrixStack();

        switch (bars) {
        case LEFT:
            // RotP's vertical bar cluster is 28px wide and centered vertically.
            // Add Insomnia as an additional, slim bar immediately beside it.
            drawVertical(stack, INDENT + ROT_P_BARS_WIDTH + GAP,
                    (height - ROT_P_BARS_HEIGHT) / 2, mc);
            break;
        case RIGHT:
            // The right-side version is mirrored alongside RotP's cluster.
            drawVertical(stack, width - INDENT - ROT_P_BARS_WIDTH - GAP - 8,
                    (height - ROT_P_BARS_HEIGHT) / 2, mc);
            break;
        case TOP_LEFT:
            drawHorizontalNextToRotP(stack, INDENT, width, false, mc);
            break;
        case TOP_RIGHT:
            drawHorizontalNextToRotP(stack, width - INDENT, width, true, mc);
            break;
        default:
            break;
        }
    }

    /**
     * 8x100px vertical meter + a 12px moon. Similar footprint to a normal
     * RotP bar; no huge background rectangle or duplicate title.
     */
    private static void drawVertical(MatrixStack stack, int x, int y, Minecraft mc) {
        final int w = 8;
        final int h = ROT_P_BARS_HEIGHT;
        int filled = Math.max(0, Math.min(h,
                Math.round(h * animatedStacks / BlueNightInsomnia.MAX_STACKS)));

        rect(stack, x - 1, y - 1, w + 2, h + 2, BAR_OUTLINE);
        rect(stack, x, y, w, h, BAR_BORDER);
        rect(stack, x + 1, y + 1, w - 2, h - 2, BAR_EMPTY);

        for (int i = 0; i < filled; i++) {
            int level = h - 1 - i;
            int color = colorForProgress((float) i / h);
            rect(stack, x + 1, y + level, w - 2, 1, color);
        }

        drawMoon(stack, x + 3, y - 10, serverStacks, mc);
        String amount = Integer.toString(serverStacks);
        mc.font.drawShadow(stack, amount, x + w / 2 - mc.font.width(amount) / 2,
                y + h + 4, LABEL_COLOR);
    }

    /**
     * In RotP's TOP_LEFT / TOP_RIGHT layout, existing horizontal bars use
     * 200px + a 12px icon. Place the compact Insomnia meter beside them,
     * not below them where it would cover RotP's hotbars.
     */
    private static void drawHorizontalNextToRotP(MatrixStack stack,
                                                   int anchorX, int screenWidth,
                                                   boolean right, Minecraft mc) {
        final int meterWidth = 96;
        final int totalWidth = meterWidth + 16;
        final int rotpWidth = ROT_P_HORIZONTAL_LENGTH + ROT_P_ICON_WIDTH + 4;
        int x = right
                ? anchorX - rotpWidth - GAP - totalWidth
                : anchorX + rotpWidth + GAP;

        // Extreme GUI scales: preserve visibility even in narrow windows.
        if (x < 2 || x + totalWidth > screenWidth - 2) {
            x = right ? Math.max(2, screenWidth - totalWidth - 2) : 2;
        }

        int y = INDENT + 2;
        drawMoon(stack, x + 6, y + 8, serverStacks, mc);

        String amount = serverStacks + "/" + BlueNightInsomnia.MAX_STACKS;
        mc.font.drawShadow(stack, "INSOMNIA", x + 16, y, LABEL_COLOR);
        mc.font.drawShadow(stack, amount,
                x + 16, y + 11, serverStacks == BlueNightInsomnia.MAX_STACKS
                        ? BAR_ICE : LABEL_COLOR);

        int barX = x + 16;
        int barY = y + 22;
        rect(stack, barX - 1, barY - 1, meterWidth + 2, 7, BAR_OUTLINE);
        rect(stack, barX, barY, meterWidth, 5, BAR_BORDER);
        rect(stack, barX + 1, barY + 1, meterWidth - 2, 3, BAR_EMPTY);

        int fill = Math.round((meterWidth - 2) * animatedStacks
                / BlueNightInsomnia.MAX_STACKS);
        for (int i = 0; i < fill; i++) {
            rect(stack, barX + 1 + i, barY + 1, 1, 3,
                    colorForProgress((float) i / meterWidth));
        }
    }

    private static int colorForProgress(float progress) {
        return progress > 0.82F ? BAR_ICE : progress > 0.50F ? BAR_CYAN : BAR_BLUE;
    }

    /** Pixel-sized moon icon that waxes as the Insomnia meter fills. */
    private static void drawMoon(MatrixStack stack, int cx, int cy,
                                 int stacks, Minecraft mc) {
        float reveal = stacks / (float) BlueNightInsomnia.MAX_STACKS;
        boolean full = stacks >= BlueNightInsomnia.MAX_STACKS;
        boolean pulse = full && (mc.level.getGameTime() / 6L) % 2L == 0;

        for (int row = -5; row <= 5; ++row) {
            int half = (int) Math.floor(Math.sqrt(25 - row * row));
            rect(stack, cx - half, cy + row, half * 2 + 1, 1, BAR_BORDER);
            int litStart = cx + half + 1 - Math.round((half * 2 + 1) * reveal);
            if (litStart <= cx + half) {
                rect(stack, Math.max(cx - half, litStart), cy + row,
                        cx + half + 1 - Math.max(cx - half, litStart), 1,
                        pulse ? 0xFFFFFFFF : BAR_ICE);
            }
        }
    }

    private static void rect(MatrixStack stack, int x, int y, int w, int h, int color) {
        if (w > 0 && h > 0) AbstractGui.fill(stack, x, y, x + w, y + h, color);
    }
}
