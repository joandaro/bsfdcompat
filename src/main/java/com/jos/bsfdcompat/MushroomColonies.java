package com.jos.bsfdcompat;

import com.legacy.blue_skies.registries.SkiesBlocks;
import java.util.function.Supplier;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.ComposterBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;
import vectorwing.farmersdelight.common.item.MushroomColonyItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class MushroomColonies {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BSFDCompat.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);

    public static final RegistryObject<SnowcapMushroomColonyBlock> SNOWCAP_MUSHROOM_COLONY =
            BLOCKS.register("snowcap_mushroom_colony", () -> new SnowcapMushroomColonyBlock(
                    BlockBehaviour.Properties.copy(ModBlocks.BROWN_MUSHROOM_COLONY.get()).randomTicks(),
                    () -> SkiesBlocks.snowcap_mushroom.asItem()));
    public static final RegistryObject<Item> SNOWCAP_MUSHROOM_COLONY_ITEM =
            ITEMS.register("snowcap_mushroom_colony", () -> new MushroomColonyItem(
                    SNOWCAP_MUSHROOM_COLONY.get(), new Item.Properties()));
    public static final RegistryObject<MushroomColonyBlock> BANEFUL_MUSHROOM_COLONY =
            colony("baneful_mushroom_colony", () -> SkiesBlocks.baneful_mushroom.asItem());

    private MushroomColonies() {
    }

    private static RegistryObject<MushroomColonyBlock> colony(String name, Supplier<Item> mushroom) {
        RegistryObject<MushroomColonyBlock> block = BLOCKS.register(name,
                () -> new MushroomColonyBlock(
                        BlockBehaviour.Properties.copy(ModBlocks.BROWN_MUSHROOM_COLONY.get()), mushroom));
        ITEMS.register(name, () -> new MushroomColonyItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }

    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> ITEMS.getEntries().forEach(
                item -> ComposterBlock.COMPOSTABLES.put(item.get(), 1.0F)));
    }
}
