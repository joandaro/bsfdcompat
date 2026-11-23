package com.jos.bsfdcompat;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public final class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);

    public static final RegistryObject<Item> SOLNUT_BUTTER_TOAST = food("solnut_butter_toast", 6, 0.6F);
    public static final RegistryObject<Item> MIXED_NUT_COOKIE = snack("mixed_nut_cookie", 3, 0.4F);
    public static final RegistryObject<Item> MIXED_NUT_BRITTLE = snack("mixed_nut_brittle", 4, 0.6F);
    public static final RegistryObject<Item> NUT_ENERGY_BAR = snack("nut_energy_bar", 6, 0.8F);
    public static final RegistryObject<Item> COMET_BERRY_TART = food("comet_berry_tart", 6, 0.5F);
    public static final RegistryObject<Item> BERRIES_COOKIE = snack("berries_cookie", 3, 0.3F);
    public static final RegistryObject<Item> PINK_BREWBERRY_MACARON = snack("pink_brewberry_macaron", 4, 0.4F);
    public static final RegistryObject<Item> BLACK_BREWBERRY_GUMMIES = snack("black_brewberry_gummies", 2, 0.2F);
    public static final RegistryObject<Item> SCALEFRUIT_TURNOVER = food("scalefruit_turnover", 6, 0.5F);
    public static final RegistryObject<Item> CRESCENT_FRUIT_BREAD = food("crescent_fruit_bread", 7, 0.6F);
    public static final RegistryObject<Item> WINTER_LEAF_ROLL = food("winter_leaf_roll", 6, 0.6F);
    public static final RegistryObject<Item> FIERY_BEAN_BUN = food("fiery_bean_bun", 6, 0.6F);
    public static final RegistryObject<Item> CRYO_ROOT_FRITTER = food("cryo_root_fritter", 5, 0.5F);
    public static final RegistryObject<Item> CARABEEF_ROLL = food("carabeef_roll", 10, 0.8F);
    public static final RegistryObject<Item> VENISON_PIE = meal("venison_pie", 10, 0.8F, 180);
    public static final RegistryObject<Item> GLAZED_MONITOR_TAIL = meal("glazed_monitor_tail", 10, 0.8F, 180);
    public static final RegistryObject<Item> BLUE_MUSHROOM_STEW = meal("blue_mushroom_stew", 6, 0.6F, 180);
    public static final RegistryObject<Item> SEAS_AND_SKIES_BURGER = food("seas_and_skies_burger", 10, 0.8F);
    public static final RegistryObject<Item> BRIGHT_DUMPLING = food("bright_dumpling", 5, 0.6F);
    public static final RegistryObject<Item> DAWN_ONIGIRI = food("dawn_onigiri", 7, 0.7F);
    public static final RegistryObject<Item> HORIZOFIN_TUNID_SUSHI = food("horizofin_tunid_sushi", 6, 0.6F);
    public static final RegistryObject<Item> GRITTLE_FLATFISH_SUSHI = food("grittle_flatfish_sushi", 6, 0.6F);
    public static final RegistryObject<Item> MUNICIPAL_MONKFISH_SUSHI = food("municipal_monkfish_sushi", 6, 0.6F);
    public static final RegistryObject<Item> CHARSCALE_MOKI_SUSHI = food("charscale_moki_sushi", 6, 0.6F);
    public static final RegistryObject<Item> VENISON_SANDWICH = food("venison_sandwich", 10, 0.8F);

    private ModItems() {
    }

    private static RegistryObject<Item> food(String name, int nutrition, float saturation) {
        return food(name, nutrition, saturation, 0, false, false);
    }

    private static RegistryObject<Item> meal(String name, int nutrition, float saturation, int adaptationSeconds) {
        return food(name, nutrition, saturation, adaptationSeconds, false, true);
    }

    private static RegistryObject<Item> snack(String name, int nutrition, float saturation) {
        return food(name, nutrition, saturation, 0, true, false);
    }

    private static RegistryObject<Item> food(String name, int nutrition, float saturation,
                                            int adaptationSeconds, boolean fast, boolean bowlFood) {
        return ITEMS.register(name, () -> {
            FoodProperties.Builder properties = new FoodProperties.Builder()
                    .nutrition(nutrition)
                    .saturationMod(saturation);
            if (fast) {
                properties.fast();
            }
            if (adaptationSeconds > 0) {
                properties.effect(() -> new MobEffectInstance(ModEffects.SKY_ADAPTATION.get(),
                        adaptationSeconds * 20, 0, false, false, true), 1.0F);
            }
            Item.Properties itemProperties = new Item.Properties().food(properties.build());
            if (bowlFood) {
                itemProperties.craftRemainder(Items.BOWL).stacksTo(16);
            }
            return new AdaptationFoodItem(itemProperties);
        });
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
