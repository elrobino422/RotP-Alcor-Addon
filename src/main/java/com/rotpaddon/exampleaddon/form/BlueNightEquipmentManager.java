
package com.rotpaddon.exampleaddon.form;

import com.github.standobyte.jojo.power.impl.stand.IStandPower;
import com.rotpaddon.exampleaddon.AddonMain;
import com.rotpaddon.exampleaddon.init.InitItems;
import com.rotpaddon.exampleaddon.init.InitStands;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.text.StringTextComponent;

import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(
        modid = AddonMain.MOD_ID,
        bus = Mod.EventBusSubscriber.Bus.FORGE
)
public final class BlueNightEquipmentManager {

    public static final String MANIFESTED_TAG =
            "AlcorBlueNightManifested";

    private BlueNightEquipmentManager() {}

    // Identifies Scythes created by Blue Night,
    // not ordinary items obtained with /give.
    private static boolean isManifestedScythe(ItemStack stack) {
        return !stack.isEmpty()
                && stack.getItem() == InitItems.BLUE_NIGHT_SCYTHE.get()
                && stack.hasTag()
                && stack.getTag().getBoolean(MANIFESTED_TAG);
    }

    // Called by the shared form-selection packet.
    public static boolean selectForm(
            ServerPlayerEntity player,
            IAlcorFormHolder holder,
            int formId) {

        // Bracelet form.
        if (formId == 0) {
            removeManifestedItems(player);
            holder.alcorSetForm(0);
            return true;
        }

        // Only Scythe is implemented for now.
        if (formId != 1) {
            return false;
        }

        ItemStack held = player.getMainHandItem();

        // Never overwrite the player's ordinary item.
        if (!held.isEmpty() && !isManifestedScythe(held)) {
            player.displayClientMessage(
                    new StringTextComponent(
                            "Empty your main hand to summon Moon Reaper."
                    ),
                    true
            );
            return false;
        }

        // Remove previously manifested copies.
        removeManifestedItems(player);

        // Create the real Minecraft item.
        ItemStack scythe = new ItemStack(
                InitItems.BLUE_NIGHT_SCYTHE.get()
        );

        // Mark this as a temporary Stand weapon.
        scythe.getOrCreateTag()
                .putBoolean(MANIFESTED_TAG, true);

        // Equip it in the main hand.
        player.setItemInHand(Hand.MAIN_HAND, scythe);

        // Synchronize the new form.
        holder.alcorSetForm(1);

        return true;
    }

    // Only removes Stand-manifested Scythes.
    public static void removeManifestedItems(PlayerEntity player) {

        for (int slot = 0;
             slot < player.inventory.getContainerSize();
             slot++) {

            ItemStack stack = player.inventory.getItem(slot);

            if (isManifestedScythe(stack)) {
                player.inventory.setItem(
                        slot,
                        ItemStack.EMPTY
                );
            }
        }
    }

    // Clean up when Blue Night is no longer summoned,
    // or when the player switches to another Stand.
    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {

        if (event.phase != TickEvent.Phase.END
                || event.player.level.isClientSide()) {
            return;
        }

        PlayerEntity player = event.player;

        IStandPower power = IStandPower
                .getStandPowerOptional(player)
                .orElse(null);

        boolean scytheShouldExist =
                power != null
                        && power.isActive()
                        && power.getType() ==
                        InitStands.STAND_BLUE_NIGHT.getStandType()
                        && power.getStandManifestation()
                        instanceof IAlcorFormHolder
                        && ((IAlcorFormHolder)
                        power.getStandManifestation())
                        .alcorGetForm() == 1;

        if (!scytheShouldExist) {
            removeManifestedItems(player);
        }
    }
}
