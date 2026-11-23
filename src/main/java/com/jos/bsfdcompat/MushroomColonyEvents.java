package com.jos.bsfdcompat;

import com.legacy.blue_skies.registries.SkiesBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.event.entity.player.BonemealEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.common.registry.ModBlocks;

/** Starts colonies during rich-soil growth or manual bonemeal, without modifying Farmer's Delight. */
@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class MushroomColonyEvents {
    private MushroomColonyEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBonemeal(BonemealEvent event) {
        if (event.getResult() != Event.Result.DEFAULT) {
            return;
        }
        if (tryStartColony(event.getLevel(), event.getPos(), event.getBlock())) {
            // Forge consumes one bonemeal for ALLOW and skips the original growth logic.
            event.setResult(Event.Result.ALLOW);
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onCropGrow(BlockEvent.CropGrowEvent.Pre event) {
        if (event.getResult() == Event.Result.DENY) {
            return;
        }
        if (event.getLevel() instanceof Level level && !level.isClientSide
                && tryStartColony(level, event.getPos(), event.getState())) {
            // Rich soil would otherwise call the old mushroom's performBonemeal on a stale state.
            event.setResult(Event.Result.DENY);
        }
    }

    public static boolean canStartColony(LevelReader level, BlockPos pos, BlockState state) {
        return findColony(level, pos, state) != null;
    }

    public static boolean tryStartColony(Level level, BlockPos pos, BlockState state) {
        ColonyTarget target = findColony(level, pos, state);
        if (target == null) {
            return false;
        }
        if (!level.isClientSide) {
            if (target.colony() instanceof SnowcapMushroomColonyBlock tallColony) {
                return tallColony.startColony(level, target.pos());
            }
            if (!level.setBlock(target.pos(), target.colony().defaultBlockState(), Block.UPDATE_ALL)) {
                return false;
            }
        }
        return true;
    }

    private static ColonyTarget findColony(LevelReader level, BlockPos pos, BlockState state) {
        if (!state.is(SkiesBlocks.snowcap_mushroom) && !state.is(SkiesBlocks.baneful_mushroom)) {
            return null;
        }
        // An earlier event handler may already have replaced the block.
        if (!level.getBlockState(pos).is(state.getBlock())) {
            return null;
        }
        state = level.getBlockState(pos);
        boolean snowcap = state.is(SkiesBlocks.snowcap_mushroom);
        Block colony;

        if (snowcap) {
            if (state.getValue(DoublePlantBlock.HALF) == DoubleBlockHalf.UPPER) {
                pos = pos.below();
                state = level.getBlockState(pos);
            }
            BlockState upper = level.getBlockState(pos.above());
            if (!state.is(SkiesBlocks.snowcap_mushroom)
                    || state.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.LOWER
                    || !upper.is(SkiesBlocks.snowcap_mushroom)
                    || upper.getValue(DoublePlantBlock.HALF) != DoubleBlockHalf.UPPER) {
                return null;
            }
            colony = MushroomColonies.SNOWCAP_MUSHROOM_COLONY.get();
        } else if (state.is(SkiesBlocks.baneful_mushroom)) {
            colony = MushroomColonies.BANEFUL_MUSHROOM_COLONY.get();
        } else {
            return null;
        }

        if (!level.getBlockState(pos.below()).is(ModBlocks.RICH_SOIL.get())) {
            return null;
        }

        BlockState seed = colony.defaultBlockState();
        if (!seed.canSurvive(level, pos)) {
            return null;
        }
        return new ColonyTarget(pos.immutable(), colony);
    }

    private record ColonyTarget(BlockPos pos, Block colony) {
    }
}
