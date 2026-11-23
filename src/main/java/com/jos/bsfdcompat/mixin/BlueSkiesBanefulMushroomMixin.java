package com.jos.bsfdcompat.mixin;

import com.jos.bsfdcompat.MushroomColonyEvents;
import com.legacy.blue_skies.blocks.natural.BanefulMushroomBlock;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.BonemealableBlock;
import net.minecraft.world.level.block.BushBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;

/** Lets rich soil recognize Baneful mushrooms as growable, only when a colony can start. */
@Mixin(value = BanefulMushroomBlock.class, remap = false)
public abstract class BlueSkiesBanefulMushroomMixin extends BushBlock implements BonemealableBlock {
    protected BlueSkiesBanefulMushroomMixin(BlockBehaviour.Properties properties) {
        super(properties);
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        return MushroomColonyEvents.canStartColony(level, pos, state);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        return true;
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        MushroomColonyEvents.tryStartColony(level, pos, state);
    }
}
