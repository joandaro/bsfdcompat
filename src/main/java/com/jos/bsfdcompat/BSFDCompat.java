package com.jos.bsfdcompat;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BSFDCompat.MODID)
public final class BSFDCompat {
    public static final String MODID = "bsfdcompat";
    public static final ResourceLocation BLUE_SKIES_TAB_ID = new ResourceLocation("blue_skies", "all_items");

    public BSFDCompat(FMLJavaModLoadingContext context) {
        IEventBus modEventBus = context.getModEventBus();
        ModEffects.EFFECTS.register(modEventBus);
        ModItems.ITEMS.register(modEventBus);
        RawCuts.ITEMS.register(modEventBus);
        KitchenRegistry.BLOCKS.register(modEventBus);
        KitchenRegistry.BLOCK_ENTITIES.register(modEventBus);
        KitchenRegistry.ITEMS.register(modEventBus);
        KitchenRegistry.RECIPE_SERIALIZERS.register(modEventBus);

        modEventBus.addListener(RawCuts::addCreativeTabContents);
        modEventBus.addListener(ModItems::addCreativeTabContents);
        modEventBus.addListener(KitchenRegistry::addCreativeTabContents);
    }
}
