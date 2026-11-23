package com.jos.bsfdcompat;

import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DoublePlantBlock;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.MushroomColonyBlock;

/** Farmer's Delight colony behavior, extended with a linked upper half. */
public final class SnowcapMushroomColonyBlock extends MushroomColonyBlock {
    public static final EnumProperty<DoubleBlockHalf> HALF = DoublePlantBlock.HALF;
    private static final VoxelShape LOWER_SHAPE = Block.box(3, 0, 3, 13, 16, 13);
    private static final VoxelShape UPPER_SHAPE = Block.box(1, 0, 1, 15, 16, 15);

    public SnowcapMushroomColonyBlock(BlockBehaviour.Properties properties, Supplier<Item> mushroomType) {
        super(properties, mushroomType);
        registerDefaultState(defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
        super.createBlockStateDefinition(builder);
        builder.add(HALF);
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? UPPER_SHAPE : LOWER_SHAPE;
    }

    @Override
    public boolean canSurvive(BlockState state, LevelReader level, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER
                ? isLower(level.getBlockState(pos.below()))
                : super.canSurvive(state, level, pos);
    }

    @Override
    @Nullable
    public BlockState getStateForPlacement(BlockPlaceContext context) {
        BlockPos pos = context.getClickedPos();
        Level level = context.getLevel();
        if (pos.getY() >= level.getMaxBuildHeight() - 1
                || !level.getBlockState(pos.above()).canBeReplaced(context)) {
            return null;
        }
        return super.getStateForPlacement(context);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        level.setBlock(pos.above(), state.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL);
    }

    @Override
    public BlockState updateShape(BlockState state, Direction direction, BlockState neighbor,
                                  LevelAccessor level, BlockPos pos, BlockPos neighborPos) {
        boolean lower = state.getValue(HALF) == DoubleBlockHalf.LOWER;
        if (direction == (lower ? Direction.UP : Direction.DOWN)) {
            if (!neighbor.is(this) || neighbor.getValue(HALF) == state.getValue(HALF)) {
                return Blocks.AIR.defaultBlockState();
            }
            // The lower half is authoritative; age changes also propagate through shape updates.
            return lower ? state : state.setValue(COLONY_AGE, neighbor.getValue(COLONY_AGE));
        }
        return super.updateShape(state, direction, neighbor, level, pos, neighborPos);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player,
                                 InteractionHand hand, BlockHitResult hit) {
        BlockPos lowerPos = lowerPosition(state, pos);
        BlockState lower = level.getBlockState(lowerPos);
        if (!isLower(lower)) {
            return InteractionResult.PASS;
        }
        InteractionResult result = super.use(lower, level, lowerPos, player, hand, hit.withPosition(lowerPos));
        syncUpper(level, lowerPos);
        return result;
    }

    @Override
    public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource random) {
        // Vanilla randomTick dispatches to tick; the FD parent implements its growth here.
        if (isLower(state)) {
            super.tick(state, level, pos, random);
            syncUpper(level, pos);
        }
    }

    @Override
    public boolean isValidBonemealTarget(LevelReader level, BlockPos pos, BlockState state, boolean isClient) {
        BlockPos lowerPos = lowerPosition(state, pos);
        BlockState lower = level.getBlockState(lowerPos);
        BlockState upper = level.getBlockState(lowerPos.above());
        return isLower(lower) && (isUpper(upper)
                || lowerPos.getY() < level.getMaxBuildHeight() - 1 && upper.isAir())
                && super.isValidBonemealTarget(level, lowerPos, lower, isClient);
    }

    @Override
    public boolean isBonemealSuccess(Level level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos lowerPos = lowerPosition(state, pos);
        BlockState lower = level.getBlockState(lowerPos);
        return isLower(lower) && super.isBonemealSuccess(level, random, lowerPos, lower);
    }

    @Override
    public void performBonemeal(ServerLevel level, RandomSource random, BlockPos pos, BlockState state) {
        BlockPos lowerPos = lowerPosition(state, pos);
        BlockState lower = level.getBlockState(lowerPos);
        if (isLower(lower)) {
            super.performBonemeal(level, random, lowerPos, lower);
            syncUpper(level, lowerPos);
        }
    }

    @Override
    public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player) {
        if (!level.isClientSide && state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockPos lowerPos = pos.below();
            BlockState lower = level.getBlockState(lowerPos);
            if (isLower(lower)) {
                if (!player.isCreative()) {
                    // Award the parent's normal mining stats and lower-only loot with the actual tool.
                    super.playerDestroy(level, player, lowerPos, lower, null, player.getMainHandItem());
                }
                level.setBlock(lowerPos, Blocks.AIR.defaultBlockState(),
                        Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                // Both halves are gone before the caller attempts its normal removal, so it cannot loot twice.
                if (isUpper(level.getBlockState(pos))) {
                    level.setBlock(pos, Blocks.AIR.defaultBlockState(),
                            Block.UPDATE_ALL | Block.UPDATE_SUPPRESS_DROPS);
                }
            }
        }
        super.playerWillDestroy(level, pos, state, player);
    }

    private BlockPos lowerPosition(BlockState state, BlockPos pos) {
        return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
    }

    private boolean isLower(BlockState state) {
        return state.is(this) && state.getValue(HALF) == DoubleBlockHalf.LOWER;
    }

    private boolean isUpper(BlockState state) {
        return state.is(this) && state.getValue(HALF) == DoubleBlockHalf.UPPER;
    }

    private void syncUpper(Level level, BlockPos lowerPos) {
        BlockState lower = level.getBlockState(lowerPos);
        BlockState upper = level.getBlockState(lowerPos.above());
        if (isLower(lower) && isUpper(upper)
                && !lower.getValue(COLONY_AGE).equals(upper.getValue(COLONY_AGE))) {
            level.setBlock(lowerPos.above(), upper.setValue(COLONY_AGE, lower.getValue(COLONY_AGE)),
                    Block.UPDATE_CLIENTS);
        }
    }

    /** Replace both original Snowcap halves before issuing any neighbor shape updates. */
    public boolean startColony(Level level, BlockPos pos) {
        BlockState oldLower = level.getBlockState(pos);
        BlockState oldUpper = level.getBlockState(pos.above());
        BlockState lower = defaultBlockState();
        int deferredUpdates = Block.UPDATE_CLIENTS | Block.UPDATE_KNOWN_SHAPE;
        if (!level.setBlock(pos, lower, deferredUpdates)) {
            return false;
        }
        if (!level.setBlock(pos.above(), lower.setValue(HALF, DoubleBlockHalf.UPPER), Block.UPDATE_ALL)) {
            level.setBlock(pos, oldLower, deferredUpdates);
            level.setBlock(pos.above(), oldUpper, Block.UPDATE_ALL);
            return false;
        }
        level.updateNeighborsAt(pos, this);
        return true;
    }
}
