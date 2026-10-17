package com.rotpaddon.exampleaddon.client.gui;

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
 * Blue Night's moon-themed Insomnia HUD.
 *
 * Rendered client-side only; the authoritative value comes from a server
 * packet, never from reading player persistent NBT on the client.
 * The location is intentionally top-left to avoid RotP's action hotbars.
 */
@Mod.EventBusSubscriber(modid = AddonMain.MOD_ID,
        value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class BlueNightInsomniaHud {
    private static final int WIDTH = 190;
    private static final int HEIGHT = 46;

    private static final int BORDER = 0xE64E85A5;
    private static final int PANEL = 0xDE090F20;
    private static final int PANEL_INNER = 0xC915233B;
    private static final int SEGMENT_EMPTY = 0xFF1D2941;
    private static final int BLUE = 0xFF3465B6;
    private static final int CYAN = 0xFF59B8E3;
    private static final int ICE = 0xFFB3E9FF;
    private static final int WHITE = 0xFFF0FAFF;

    private static int serverStacks;
    private static float animatedStacks;

    private BlueNightInsomniaHud() {}

    /** Called only on the client thread by BlueNightInsomniaSyncPacket. */
    public static void receive(int stacks) {
        serverStacks = Math.max(0, Math.min(BlueNightInsomnia.MAX_STACKS, stacks));
    }

    @SubscribeEvent
    public static void onLogout(ClientPlayerNetworkEvent.LoggedOutEvent event) {
        serverStacks = 0;
        animatedStacks = 0;
    }

    @SubscribeEvent
    public static void onRespawn(ClientPlayerNetworkEvent.RespawnEvent event) {
        // The server re-sends the new player's authoritative resource.
        serverStacks = 0;
        animatedStacks = 0;
    }

    @SubscribeEvent
    public static void render(RenderGameOverlayEvent.Post event) {
        if (event.getType() != RenderGameOverlayEvent.ElementType.ALL) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.options.hideGui) return;

        boolean blueNightActive = IStandPower.getStandPowerOptional(mc.player)
                .map(power -> power.isActive()
                        && power.getType() == InitStands.STAND_BLUE_NIGHT.getStandType())
                .orElse(false);
        if (!blueNightActive) return;

        MatrixStack stack = event.getMatrixStack();
        int x = 12;
        int y = 12;
        if (event.getWindow().getGuiScaledWidth() < WIDTH + 24) x = 4;

        // Smooth value change; gameplay remains server-authoritative.
        animatedStacks += (serverStacks - animatedStacks) * 0.16F;
        if (Math.abs(animatedStacks - serverStacks) < 0.05F) {
            animatedStacks = serverStacks;
        }

        boolean fullMoon = serverStacks >= BlueNightInsomnia.MAX_STACKS;
        float pulse = fullMoon
                ? 0.5F + 0.5F * (float) Math.sin(
                        (mc.level.getGameTime() + event.getPartialTicks()) * 0.14D)
                : 0.0F;

        // Outer border, dark panel and moon-blue trim.
        rect(stack, x, y, WIDTH, HEIGHT, BORDER);
        rect(stack, x + 1, y + 1, WIDTH - 2, HEIGHT - 2, PANEL);
        rect(stack, x + 2, y + 2, WIDTH - 4, 2, 0xFF32527D);
        rect(stack, x + 1, y + HEIGHT - 4, WIDTH - 2, 3, PANEL_INNER);

        drawMoon(stack, x + 16, y + 23, fullMoon, pulse);

        int barX = x + 34;
        int barY = y + 31;
        final int segmentWidth = 12;
        final int segmentGap = 2;
        final int segments = 10;

        mc.font.drawShadow(stack, "BLUE NIGHT", barX, y + 7, 0xFFB3D8E5);
        mc.font.drawShadow(stack, "INSOMNIA", barX, y + 19, WHITE);
        String amount = serverStacks + "/" + BlueNightInsomnia.MAX_STACKS;
        mc.font.drawShadow(stack, amount,
                x + WIDTH - 8 - mc.font.width(amount), y + 19,
                fullMoon ? ICE : 0xFFB3D8E5);

        // Ten segmented units, with partial filling inside each unit.
        float perSegment = BlueNightInsomnia.MAX_STACKS / (float) segments;
        for (int i = 0; i < segments; ++i) {
            int sx = barX + i * (segmentWidth + segmentGap);
            rect(stack, sx - 1, barY - 1, segmentWidth + 2, 8, 0xFF101A2D);
            rect(stack, sx, barY, segmentWidth, 6, SEGMENT_EMPTY);

            float segmentFill = Math.max(0.0F,
                    Math.min(1.0F, (animatedStacks - i * perSegment) / perSegment));
            int px = (int) Math.ceil(segmentWidth * segmentFill);
            if (px > 0) {
                int tint = i >= 8 ? ICE : (i >= 5 ? CYAN : BLUE);
                rect(stack, sx, barY, px, 6, tint);
                rect(stack, sx, barY, px, 1, 0x88E8FAFF);
            }
        }

        if (fullMoon && pulse > 0.55F) {
            // Pixel glints that animate gently at maximum Insomnia.
            rect(stack, x + 181, y + 6, 2, 2, ICE);
            rect(stack, x + 177, y + 10, 1, 1, WHITE);
            rect(stack, x + 9, y + 6, 2, 2, ICE);
        }
    }

    private static void drawMoon(MatrixStack stack, int cx, int cy,
                                 boolean full, float pulse) {
        // Pixelated full-disc silhouette, gradually revealed from the right.
        for (int dy = -8; dy <= 8; ++dy) {
            int half = (int) Math.floor(Math.sqrt(64 - dy * dy));
            if (half == 0) continue;

            rect(stack, cx - half, cy + dy, 2 * half + 1, 1, 0xFF365473);

            float reveal = serverStacks / (float) BlueNightInsomnia.MAX_STACKS;
            int firstLit = cx + half - Math.round(2.0F * half * reveal);
            int left = Math.max(cx - half, firstLit);
            int right = cx + half + 1;
            if (right > left) {
                rect(stack, left, cy + dy, right - left, 1,
                        full && pulse > 0.55F ? WHITE : ICE);
            }
        }
        // Tiny pale highlight, even when the resource is empty.
        rect(stack, cx + 5, cy - 6, 1, 2, CYAN);
    }

    private static void rect(MatrixStack stack, int x, int y, int w, int h, int color) {
        if (w > 0 && h > 0) {
            AbstractGui.fill(stack, x, y, x + w, y + h, color);
        }
    }
}
