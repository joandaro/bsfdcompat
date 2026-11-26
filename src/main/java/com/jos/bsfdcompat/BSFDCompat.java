package com.jos.bsfdcompat;

import com.jos.bsfdcompat.registry.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import com.jos.bsfdcompat.registry.SoilRegistry;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.ForgeRegistries;

@Mod(BSFDCompat.MODID)
public final class BSFDCompat {
    public static final String MODID = "bsfdcompat";
    public static final ResourceLocation BLUE_SKIES_TAB_ID = new ResourceLocation("blue_skies", "all_items");
    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec.BooleanValue ENABLE_FEAST_PICKUP = CONFIG_BUILDER
            .comment("Allow empty-hand left-click pickup of Farmer's Delight feast blocks, preserving remaining servings.")
            .worldRestart()
            .define("enableFeastPickup", true);
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

    public BSFDCompat(FMLJavaModLoadingContext context) {
        context.registerConfig(ModConfig.Type.SERVER, CONFIG_SPEC);
        IEventBus modEventBus = context.getModEventBus();
        ModEffects.EFFECTS.register(modEventBus);
        FoodItems.ITEMS.register(modEventBus);
        FeastRegistry.BLOCKS.register(modEventBus);
        FeastRegistry.ITEMS.register(modEventBus);
        CropCrates.BLOCKS.register(modEventBus);
        CropCrates.ITEMS.register(modEventBus);
        MushroomColonies.BLOCKS.register(modEventBus);
        MushroomColonies.ITEMS.register(modEventBus);
        RawCuts.ITEMS.register(modEventBus);
        KitchenRegistry.BLOCKS.register(modEventBus);
        KitchenRegistry.BLOCK_ENTITIES.register(modEventBus);
        KitchenRegistry.ITEMS.register(modEventBus);
        KitchenRegistry.RECIPE_SERIALIZERS.register(modEventBus);
        SoilRegistry.BLOCKS.register(modEventBus);
        SoilRegistry.ITEMS.register(modEventBus);

        modEventBus.addListener(SoilRegistry::addCreativeTabContents);
        modEventBus.addListener(RawCuts::addCreativeTabContents);
        modEventBus.addListener(FoodItems::addCreativeTabContents);
        modEventBus.addListener(FeastRegistry::addCreativeTabContents);
        modEventBus.addListener(CropCrates::addCreativeTabContents);
        modEventBus.addListener(MushroomColonies::addCreativeTabContents);
        modEventBus.addListener(MushroomColonies::commonSetup);
        modEventBus.addListener(KitchenRegistry::addCreativeTabContents);
        modEventBus.addListener((FMLCommonSetupEvent event) ->
                event.enqueueWork(() ->
                        ComposterBlock.COMPOSTABLES.put(
                                ForgeRegistries.ITEMS.getValue(
                                        new ResourceLocation("blue_skies", "bug_guts")),
                                0.65F)));
    }
}
