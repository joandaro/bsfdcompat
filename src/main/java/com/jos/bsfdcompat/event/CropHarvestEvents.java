package com.jos.bsfdcompat.event;

import com.jos.bsfdcompat.BSFDCompat;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import net.minecraftforge.registries.ForgeRegistries;

@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CropHarvestEvents {
    private static final TagKey<Item> KNIVES = ItemTags.create(new ResourceLocation("forge", "tools/knives"));
    private static final Method DEFAULT_USE = ObfuscationReflectionHelper.findMethod(BlockBehaviour.class,
            "m_6227_", BlockState.class, Level.class, BlockPos.class, Player.class,
            InteractionHand.class, BlockHitResult.class);
    private static final Map<Class<?>, Boolean> HAS_CUSTOM_USE = new ConcurrentHashMap<>();

    private static final ForgeConfigSpec.Builder CONFIG_BUILDER = new ForgeConfigSpec.Builder();
    private static final ForgeConfigSpec.ConfigValue<List<? extends String>> EXCLUDED_DROPS = CONFIG_BUILDER
            .comment("Item IDs removed from right-click crop harvest drops.",
                    "Add seeds from other mods here. Items that are both the crop and its planting material",
                    "(carrots, potatoes, onions, rice, cryo roots, etc.) are intentionally not excluded.")
            .defineListAllowEmpty("excludedDrops", List.of(
                    "minecraft:wheat_seeds",
                    "minecraft:beetroot_seeds",
                    "minecraft:melon_seeds",
                    "minecraft:pumpkin_seeds",
                    "minecraft:torchflower_seeds",
                    "minecraft:pitcher_pod",
                    "farmersdelight:cabbage_seeds",
                    "farmersdelight:tomato_seeds",
                    "blue_skies:winter_leaf_seeds",
                    "blue_skies:fiery_bean_seeds",
                    "blue_skies:scalefruit_seeds",
                    "blue_skies:pine_fruit_seeds"
            ), value -> value instanceof String id && ResourceLocation.tryParse(id) != null);
    private static final ForgeConfigSpec CONFIG_SPEC = CONFIG_BUILDER.build();

    private CropHarvestEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void harvest(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND
                || event.getUseBlock() == Event.Result.DENY || event.getUseItem() == Event.Result.DENY) {
            return;
        }
        ItemStack tool = event.getItemStack();
        if (!tool.is(KNIVES) && !tool.is(ItemTags.HOES)) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof CropBlock crop) || !crop.isMaxAge(state)) {
            return;
        }
        if (!(crop.getStateDefinition().getProperty("age") instanceof IntegerProperty age)
                || !age.getPossibleValues().contains(0)) {
            return;
        }
        if (state.hasProperty(BlockStateProperties.DOUBLE_BLOCK_HALF)
                && state.getValue(BlockStateProperties.DOUBLE_BLOCK_HALF) == DoubleBlockHalf.LOWER) {
            return;
        }
        if (HAS_CUSTOM_USE.computeIfAbsent(crop.getClass(), CropHarvestEvents::hasCustomUse)) {
            return;
        }
        Player player = event.getEntity();
        if (player.isSpectator() || !player.mayBuild() || !level.mayInteract(player, pos)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        if (!(level instanceof ServerLevel server)) {
            return;
        }
        List<ItemStack> drops = Block.getDrops(state, server, pos, server.getBlockEntity(pos), player, tool);
        if (server.setBlock(pos, state.setValue(age, 0), Block.UPDATE_ALL)) {
            List<? extends String> excluded = EXCLUDED_DROPS.get();
            for (ItemStack drop : drops) {
                ResourceLocation id = ForgeRegistries.ITEMS.getKey(drop.getItem());
                if (id == null || !excluded.contains(id.toString())) {
                    Block.popResource(server, pos, drop);
                }
            }
            server.playSound(null, pos, SoundEvents.CROP_BREAK, SoundSource.BLOCKS, 1.0F, 1.0F);
        }
    }

    private static boolean hasCustomUse(Class<?> type) {
        try {
            return type.getMethod(DEFAULT_USE.getName(), DEFAULT_USE.getParameterTypes()).getDeclaringClass()
                    != DEFAULT_USE.getDeclaringClass();
        } catch (NoSuchMethodException | SecurityException exception) {
            // If inspection fails, leave that crop to its original interaction.
            return true;
        }
    }

    /** Registers the separate config during construction, before Forge loads common configs. */
    @Mod.EventBusSubscriber(modid = BSFDCompat.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static final class ConfigRegistration {
        private ConfigRegistration() {
        }

        @SubscribeEvent
        public static void registerConfig(FMLConstructModEvent event) {
            ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, CONFIG_SPEC,
                    "bsfdcompat-crop-harvest-common.toml");
        }
    }
}
