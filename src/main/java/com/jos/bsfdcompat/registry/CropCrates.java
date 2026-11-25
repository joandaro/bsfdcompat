package com.jos.bsfdcompat.registry;

import com.jos.bsfdcompat.BSFDCompat;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class CropCrates {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, BSFDCompat.MODID);
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);

    public static final RegistryObject<Block> CRYO_ROOT_CRATE = crate("cryo_root_crate");
    public static final RegistryObject<Block> FIERY_BEAN_CRATE = crate("fiery_bean_crate");
    public static final RegistryObject<Block> PINE_FRUIT_CRATE = crate("pine_fruit_crate");
    public static final RegistryObject<Block> SCALEFRUIT_CRATE = crate("scalefruit_crate");
    public static final RegistryObject<Block> SOLNUT_CRATE = crate("solnut_crate");
    public static final RegistryObject<Block> WINTER_LEAF_CRATE = crate("winter_leaf_crate");

    private CropCrates() {
    }

    private static RegistryObject<Block> crate(String name) {
        RegistryObject<Block> block = BLOCKS.register(name,
                () -> new Block(BlockBehaviour.Properties.copy(ModBlocks.CARROT_CRATE.get())));
        ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
        return block;
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            ITEMS.getEntries().forEach(item -> event.accept(item.get()));
        }
    }
}
