package com.jos.bsfdcompat.registry;

import com.jos.bsfdcompat.item.AdaptationFoodItem;
import com.jos.bsfdcompat.BSFDCompat;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class RawCuts {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);

    public static final RegistryObject<Item> GRITTLE_FLATFISH_SLICE = food("grittle_flatfish_slice", 2, 0.1F, false);
    public static final RegistryObject<Item> MUNICIPAL_MONKFISH_SLICE = food("municipal_monkfish_slice", 1, 0.1F, false);
    public static final RegistryObject<Item> CHARSCALE_MOKI_SLICE = food("charscale_moki_slice", 1, 0.1F, false);
    public static final RegistryObject<Item> HORIZOFIN_TUNID_SLICE = food("horizofin_tunid_slice", 1, 0.1F, false);
    public static final RegistryObject<Item> CARABEEF_CUTS = food("carabeef_cuts", 2, 0.3F, true);
    public static final RegistryObject<Item> VENISON_CUTS = food("venison_cuts", 2, 0.3F, true);
    public static final RegistryObject<Item> MONITOR_TAIL_CUTS = food("monitor_tail_cuts", 2, 0.3F, true);

    public static final RegistryObject<Item> COOKED_GRITTLE_FLATFISH_SLICE = food("cooked_grittle_flatfish_slice", 4, 0.6F, false);
    public static final RegistryObject<Item> COOKED_MUNICIPAL_MONKFISH_SLICE = food("cooked_municipal_monkfish_slice", 3, 0.6F, false);
    public static final RegistryObject<Item> COOKED_CHARSCALE_MOKI_SLICE = food("cooked_charscale_moki_slice", 3, 0.6F, false);
    public static final RegistryObject<Item> COOKED_HORIZOFIN_TUNID_SLICE = food("cooked_horizofin_tunid_slice", 3, 0.6F, false);
    public static final RegistryObject<Item> COOKED_CARABEEF_CUTS = food("cooked_carabeef_cuts", 4, 0.8F, true);
    public static final RegistryObject<Item> COOKED_VENISON_CUTS = food("cooked_venison_cuts", 4, 0.8F, true);
    public static final RegistryObject<Item> COOKED_MONITOR_TAIL_CUTS = food("cooked_monitor_tail_cuts", 4, 0.8F, true);

    private RawCuts() {
    }

    private static RegistryObject<Item> food(String name, int nutrition, float saturation, boolean meat) {
        return ITEMS.register(name, () -> {
            FoodProperties.Builder properties = new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationMod(saturation);
            if (meat) {
                properties.meat();
            }
            return new AdaptationFoodItem(new Item.Properties().food(properties.build()));
        });
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
