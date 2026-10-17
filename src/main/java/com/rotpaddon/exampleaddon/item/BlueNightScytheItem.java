
package com.rotpaddon.exampleaddon.item;

import com.rotpaddon.exampleaddon.form.BlueNightEquipmentManager;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class BlueNightScytheItem extends Item {

    public BlueNightScytheItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean onDroppedByPlayer(
            ItemStack stack,
            PlayerEntity player) {

        // Regular Scythes obtained with /give can be dropped.
        // Manifested Scythes cannot.
        return !stack.hasTag()
                || !stack.getTag().getBoolean(
                BlueNightEquipmentManager.MANIFESTED_TAG
        );
    }
}
