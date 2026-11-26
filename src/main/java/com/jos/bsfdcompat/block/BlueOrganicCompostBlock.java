package com.jos.bsfdcompat.block;

import com.jos.bsfdcompat.registry.SoilRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import vectorwing.farmersdelight.common.block.OrganicCompostBlock;

public final class BlueOrganicCompostBlock extends OrganicCompostBlock {
    public BlueOrganicCompostBlock(Properties properties) {
        super(properties);
        registerDefaultState(defaultBlockState().setValue(SoilRegistry.EVERDAWN, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(SoilRegistry.EVERDAWN);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockState state = super.getStateForPlacement(context);
        return state == null ? null : SoilRegistry.withItemVariant(state, context.getItemInHand());
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return SoilRegistry.stackForVariant(asItem(), state.getValue(SoilRegistry.EVERDAWN));
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        SoilRegistry.finishRichSoilConversion(level, pos, state);
    }
}
