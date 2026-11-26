package com.jos.bsfdcompat.block;

import com.jos.bsfdcompat.registry.SoilRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import vectorwing.farmersdelight.common.block.RichSoilFarmlandBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class BlueRichSoilFarmlandBlock extends RichSoilFarmlandBlock {
    public BlueRichSoilFarmlandBlock(Properties properties) {
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
        if (state != null && state.is(ModBlocks.RICH_SOIL.get())) {
            state = SoilRegistry.BLUE_SKIES_RICH_SOIL.get().defaultBlockState();
        }
        return state == null ? null : SoilRegistry.withItemVariant(state, context.getItemInHand());
    }

    @Override
    public ItemStack getCloneItemStack(BlockGetter level, BlockPos pos, BlockState state) {
        return SoilRegistry.stackForVariant(asItem(), state.getValue(SoilRegistry.EVERDAWN));
    }

    @Override
    public boolean isFertile(BlockState state, BlockGetter level, BlockPos pos) {
        return state.is(this) && state.getValue(BlockStateProperties.MOISTURE) > 0;
    }

    @Override
    public void randomTick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.randomTick(state, level, pos, random);
        SoilRegistry.finishRichSoilConversion(level, pos, state);
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        super.tick(state, level, pos, random);
        SoilRegistry.finishRichSoilConversion(level, pos, state);
    }

    @Override
    public void fallOn(Level level, BlockState state, BlockPos pos, Entity entity, float fallDistance) {
        super.fallOn(level, state, pos, entity, fallDistance);
        SoilRegistry.finishRichSoilConversion(level, pos, state);
    }
}
