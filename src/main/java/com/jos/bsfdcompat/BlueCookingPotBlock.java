package com.jos.bsfdcompat;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.CookingPotBlock;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;

public final class BlueCookingPotBlock extends CookingPotBlock {
    public BlueCookingPotBlock(Properties properties) {
        super(properties);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new BlueCookingPotBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        if (level.isClientSide) {
            return createTickerHelper(type, KitchenRegistry.COOKING_POT_BLOCK_ENTITY.get(), CookingPotBlockEntity::animationTick);
        }
        return createTickerHelper(type, KitchenRegistry.COOKING_POT_BLOCK_ENTITY.get(), BlueCookingPotBlockEntity::serverTick);
    }
}
