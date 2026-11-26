package com.jos.bsfdcompat.block;

import com.jos.bsfdcompat.registry.SoilRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraftforge.common.ToolAction;
import vectorwing.farmersdelight.common.block.RichSoilBlock;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public final class BlueRichSoilBlock extends RichSoilBlock {
    public BlueRichSoilBlock(Properties properties) {
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
    public BlockState getToolModifiedState(BlockState state, UseOnContext context, ToolAction action, boolean simulate) {
        BlockState modified = super.getToolModifiedState(state, context, action, simulate);
        if (modified != null && modified.is(ModBlocks.RICH_SOIL_FARMLAND.get())) {
            return SoilRegistry.BLUE_SKIES_RICH_SOIL_FARMLAND.get().defaultBlockState()
                    .setValue(SoilRegistry.EVERDAWN, state.getValue(SoilRegistry.EVERDAWN))
                    .setValue(BlockStateProperties.MOISTURE, modified.getValue(BlockStateProperties.MOISTURE));
        }
        return modified;
    }
}
