package com.jos.bsfdcompat;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class FeastRegistry {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BSFDCompat.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);

    public static final RegistryObject<Item> STARDUST_SHEPHERDS_PIE = serving("stardust_shepherds_pie");
    public static final RegistryObject<Item> COMET_HONEY_GLAZED_MONITOR_TAIL = serving("comet_honey_glazed_monitor_tail");
    public static final RegistryObject<PortableFeastBlock> STARDUST_SHEPHERDS_PIE_BLOCK =
            BLOCKS.register("stardust_shepherds_pie_block", () -> new PortableFeastBlock(
                    BlockBehaviour.Properties.copy(ModBlocks.SHEPHERDS_PIE_BLOCK.get()),
                    STARDUST_SHEPHERDS_PIE, ModBlocks.SHEPHERDS_PIE_BLOCK));
    public static final RegistryObject<PortableFeastBlock> COMET_HONEY_GLAZED_MONITOR_TAIL_BLOCK =
            BLOCKS.register("comet_honey_glazed_monitor_tail_block", () -> new PortableFeastBlock(
                    BlockBehaviour.Properties.copy(ModBlocks.HONEY_GLAZED_HAM_BLOCK.get()),
                    COMET_HONEY_GLAZED_MONITOR_TAIL, ModBlocks.HONEY_GLAZED_HAM_BLOCK));
    public static final RegistryObject<Item> STARDUST_SHEPHERDS_PIE_FEAST =
            ITEMS.register("stardust_shepherds_pie_block", () -> new PortableFeastItem(
                    STARDUST_SHEPHERDS_PIE_BLOCK.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<Item> COMET_HONEY_GLAZED_MONITOR_TAIL_FEAST =
            ITEMS.register("comet_honey_glazed_monitor_tail_block", () -> new PortableFeastItem(
                    COMET_HONEY_GLAZED_MONITOR_TAIL_BLOCK.get(), new Item.Properties().stacksTo(1)));

    private FeastRegistry() {
    }

    private static RegistryObject<Item> serving(String name) {
        return ITEMS.register(name, () -> new AdaptationFoodItem(new Item.Properties()
                .food(new FoodProperties.Builder().nutrition(14).saturationMod(0.75F)
                        .effect(() -> new MobEffectInstance(ModEffects.SKY_ADAPTATION.get(),
                                300 * 20, 0, false, false, true), 1.0F).build())
                .craftRemainder(Items.BOWL).stacksTo(16)));
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            event.accept(STARDUST_SHEPHERDS_PIE_FEAST.get());
            event.accept(STARDUST_SHEPHERDS_PIE.get());
            event.accept(COMET_HONEY_GLAZED_MONITOR_TAIL_FEAST.get());
            event.accept(COMET_HONEY_GLAZED_MONITOR_TAIL.get());
        }
    }
}
