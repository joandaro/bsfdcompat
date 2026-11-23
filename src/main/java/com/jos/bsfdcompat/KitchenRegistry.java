package com.jos.bsfdcompat;

import com.legacy.blue_skies.items.util.SkiesItemTier;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.item.CookingPotItem;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class KitchenRegistry {
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BSFDCompat.MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BSFDCompat.MODID);
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BSFDCompat.MODID);

    public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BSFDCompat.MODID);
    public static final RegistryObject<RecipeSerializer<BlueKnifeRecipe>> KNIFE_WITH_HANDLE =
            RECIPE_SERIALIZERS.register("knife_with_handle", BlueKnifeRecipe.Serializer::new);

    public static final RegistryObject<Block> BLUE_SKIES_COOKING_POT = BLOCKS.register("blue_skies_cooking_pot",
            () -> new BlueCookingPotBlock(BlockBehaviour.Properties.copy(ModBlocks.COOKING_POT.get())));
    public static final RegistryObject<Item> BLUE_SKIES_COOKING_POT_ITEM = ITEMS.register("blue_skies_cooking_pot",
            () -> new CookingPotItem(BLUE_SKIES_COOKING_POT.get(), new Item.Properties().stacksTo(1)));
    public static final RegistryObject<BlockEntityType<BlueCookingPotBlockEntity>> COOKING_POT_BLOCK_ENTITY =
            BLOCK_ENTITIES.register("blue_skies_cooking_pot", () -> BlockEntityType.Builder.of(
                    BlueCookingPotBlockEntity::new, BLUE_SKIES_COOKING_POT.get()).build(null));

    public static final RegistryObject<Item> AQUITE_KNIFE = knife("aquite_knife", SkiesItemTier.AQUITE, 3.5F, 2.0F);
    public static final RegistryObject<Item> PYROPE_KNIFE = knife("pyrope_knife", SkiesItemTier.PYROPE, 3.5F, 2.4F);
    public static final RegistryObject<Item> DIOPSIDE_KNIFE = knife("diopside_knife", SkiesItemTier.DIOPSIDE, 5.5F, 1.6F);
    public static final RegistryObject<Item> CHAROITE_KNIFE = knife("charoite_knife", SkiesItemTier.CHAROITE, 4.5F, 2.0F);
    public static final RegistryObject<Item> HORIZONITE_KNIFE = knife("horizonite_knife", SkiesItemTier.HORIZONITE, 3.5F, 2.0F);

    private KitchenRegistry() {
    }

    private static RegistryObject<Item> knife(String name, Tier tier, float totalDamage, float attacksPerSecond) {
        // DiggerItem adds the tier bonus; the player supplies base damage 1 and base attack speed 4.
        return ITEMS.register(name, () -> new BlueKnifeItem(tier,
                totalDamage - 1.0F - tier.getAttackDamageBonus(),
                attacksPerSecond - 4.0F, new Item.Properties()));
    }

    public static void addCreativeTabContents(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey().location().equals(BSFDCompat.BLUE_SKIES_TAB_ID)) {
            event.accept(AQUITE_KNIFE.get().getDefaultInstance());
            event.accept(PYROPE_KNIFE.get().getDefaultInstance());
            event.accept(DIOPSIDE_KNIFE.get().getDefaultInstance());
            event.accept(CHAROITE_KNIFE.get().getDefaultInstance());
            event.accept(HORIZONITE_KNIFE.get().getDefaultInstance());
            event.accept(BLUE_SKIES_COOKING_POT_ITEM.get());
        }
    }
}
