
package com.rotpaddon.exampleaddon.init;

import com.rotpaddon.exampleaddon.AddonMain;

import net.minecraft.item.Item;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import com.rotpaddon.exampleaddon.item.BlueNightScytheItem;

public class InitItems {

    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(
                    ForgeRegistries.ITEMS,
                    AddonMain.MOD_ID
            );


    public static final RegistryObject<Item> BLUE_NIGHT_SCYTHE =
            ITEMS.register("blue_night_scythe",
                    () -> new BlueNightScytheItem(
                            new Item.Properties()
                                    .stacksTo(1)
                    ));

}
