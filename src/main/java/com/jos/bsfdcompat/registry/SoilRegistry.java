package com.jos.bsfdcompat.registry;

import com.jos.bsfdcompat.BSFDCompat;
import com.jos.bsfdcompat.block.BlueOrganicCompostBlock;
import com.jos.bsfdcompat.block.BlueRichSoilBlock;
import com.jos.bsfdcompat.block.BlueRichSoilFarmlandBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class SoilRegistry {
    public static final BooleanProperty EVERDAWN = BooleanProperty.create("everdawn");
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BSFDCompat.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);

    public static final RegistryObject<BlueOrganicCompostBlock> BLUE_SKIES_ORGANIC_COMPOST = BLOCKS.register(
            "blue_skies_organic_compost", () -> new BlueOrganicCompostBlock(
                    BlockBehaviour.Properties.copy(ModBlocks.ORGANIC_COMPOST.get())));
    public static final RegistryObject<BlueRichSoilBlock> BLUE_SKIES_RICH_SOIL = BLOCKS.register(
            "blue_skies_rich_soil", () -> new BlueRichSoilBlock(
                    BlockBehaviour.Properties.copy(ModBlocks.RICH_SOIL.get())));
    public static final RegistryObject<BlueRichSoilFarmlandBlock> BLUE_SKIES_RICH_SOIL_FARMLAND = BLOCKS.register(
            "blue_skies_rich_soil_farmland", () -> new BlueRichSoilFarmlandBlock(
                    BlockBehaviour.Properties.copy(ModBlocks.RICH_SOIL_FARMLAND.get())));

    public static final RegistryObject<Item> ORGANIC_COMPOST_ITEM = ITEMS.register("blue_skies_organic_compost",
            () -> new BlockItem(BLUE_SKIES_ORGANIC_COMPOST.get(), new Item.Properties()));
    public static final RegistryObject<Item> RICH_SOIL_ITEM =
            ITEMS.register("blue_skies_rich_soil",
                    () -> namedSoilItem(BLUE_SKIES_RICH_SOIL.get(), "rich_soil"));

    public static final RegistryObject<Item> RICH_SOIL_FARMLAND_ITEM =
            ITEMS.register("blue_skies_rich_soil_farmland",
                    () -> namedSoilItem(
                            BLUE_SKIES_RICH_SOIL_FARMLAND.get(),
                            "rich_soil_farmland"));

    private SoilRegistry() {
    }

    public static boolean isEverdawn(ItemStack stack) {
        CompoundTag tag = stack.getTagElement("BlockStateTag");
        return tag != null && Boolean.parseBoolean(tag.getString(EVERDAWN.getName()));
    }

    public static BlockState withItemVariant(BlockState state, ItemStack stack) {
        return state.setValue(EVERDAWN, isEverdawn(stack));
    }

    public static ItemStack stackForVariant(Item item, boolean everdawn) {
        ItemStack stack = new ItemStack(item);
        stack.getOrCreateTagElement("BlockStateTag").putString(EVERDAWN.getName(), Boolean.toString(everdawn));
        return stack;
    }

    private static BlockItem namedSoilItem(Block block, String name) {
        return new BlockItem(block, new Item.Properties()) {
            @Override
            public String getDescriptionId(ItemStack stack) {
                String material = isEverdawn(stack) ? "lunar_" : "turquoise_";
                return "block." + BSFDCompat.MODID + "." + material + name;
            }
        };
    }

    /** Redirects the original class's completed conversion to this mod's rich soil. */
    public static void finishRichSoilConversion(Level level, BlockPos pos, BlockState previous) {
        if (level.getBlockState(pos).is(ModBlocks.RICH_SOIL.get())) {
            level.setBlock(pos, BLUE_SKIES_RICH_SOIL.get().defaultBlockState()
                    .setValue(EVERDAWN, previous.getValue(EVERDAWN)), Block.UPDATE_ALL);
        }
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            ITEMS.getEntries().forEach(item -> {
                event.accept(stackForVariant(item.get(), false));
                event.accept(stackForVariant(item.get(), true));
            });
        }
    }
}
