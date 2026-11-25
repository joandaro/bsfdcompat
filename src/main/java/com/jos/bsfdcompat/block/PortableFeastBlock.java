package com.jos.bsfdcompat.block;

import java.util.function.Supplier;

import com.jos.bsfdcompat.registry.FeastRegistry;
import com.jos.bsfdcompat.registry.ModEffects;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import vectorwing.farmersdelight.common.block.FeastBlock;

/** FD serving behavior, plus direct eating and state-preserving whole-dish pickup. */
public final class PortableFeastBlock extends FeastBlock {
    private static final VoxelShape TAIL_FULL_TRAY = Block.box(1, 0, 1, 15, 2, 15);
    private static final VoxelShape TAIL_TRAY = Shapes.or(
            Block.box(1, 0, 1, 15, 1, 15),
            Block.box(2, 1, 1, 14, 2, 2),
            Block.box(2, 1, 14, 14, 2, 15),
            Block.box(1, 1, 1, 2, 2, 15),
            Block.box(14, 1, 1, 15, 2, 15));
    // Indexed by servings, then north/east/south/west.
    private static final VoxelShape[][] MONITOR_TAIL_SHAPES = {
            {TAIL_TRAY, TAIL_TRAY, TAIL_TRAY, TAIL_TRAY},
            facingShapes(7.25, 2.25, 5, 9.5, 4.75, 7.25),
            facingShapes(5, 2, 6, 11, 7, 10),
            facingShapes(4, 2, 5, 12, 8, 8),
            facingShapes(4, 2, 2, 12, 8, 8)
    };
    private final Supplier<Block> shapeTemplate;

    public PortableFeastBlock(BlockBehaviour.Properties properties, Supplier<Item> serving,
                              Supplier<Block> shapeTemplate) {
        super(properties, serving, true);
        this.shapeTemplate = shapeTemplate;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
        if (this == FeastRegistry.COMET_HONEY_GLAZED_MONITOR_TAIL_BLOCK.get()) {
            int facing = switch (state.getValue(FACING)) {
                case EAST -> 1;
                case SOUTH -> 2;
                case WEST -> 3;
                default -> 0;
            };
            return MONITOR_TAIL_SHAPES[state.getValue(getServingsProperty())][facing];
        }
        BlockState template = shapeTemplate.get().defaultBlockState()
                .setValue(FACING, state.getValue(FACING))
                .setValue(SERVINGS, state.getValue(getServingsProperty()));
        return template.getShape(level, pos, context);
    }

    private static VoxelShape[] facingShapes(double minX, double minY, double minZ,
                                             double maxX, double maxY, double maxZ) {
        return new VoxelShape[] {
                Shapes.or(TAIL_FULL_TRAY, Block.box(minX, minY, minZ, maxX, maxY, maxZ)),
                Shapes.or(TAIL_FULL_TRAY, Block.box(16 - maxZ, minY, minX, 16 - minZ, maxY, maxX)),
                Shapes.or(TAIL_FULL_TRAY, Block.box(16 - maxX, minY, 16 - maxZ, 16 - minX, maxY, 16 - minZ)),
                Shapes.or(TAIL_FULL_TRAY, Block.box(minZ, minY, 16 - maxX, maxZ, maxY, 16 - minX))
        };
    }

    @Override
    protected InteractionResult takeServing(Level level, BlockPos pos, BlockState state,
                                            Player player, InteractionHand hand) {
        int remaining = state.getValue(getServingsProperty());
        ItemStack serving = getServingItem(state);
        ItemStack container = serving.getCraftingRemainingItem();

        // Let the offhand bowl handle the interaction before an empty main hand eats a serving.
        if (remaining > 0 && hand == InteractionHand.MAIN_HAND && !container.isEmpty()
                && !ItemStack.isSameItem(player.getMainHandItem(), container)
                && ItemStack.isSameItem(player.getOffhandItem(), container)) {
            return InteractionResult.PASS;
        }
        ItemStack held = player.getItemInHand(hand);
        if (remaining > 0 && !held.isEmpty() && !container.isEmpty()
                && !ItemStack.isSameItem(held, container)) {
            // An empty offhand can still eat.
            if (!level.isClientSide && hand == InteractionHand.MAIN_HAND
                    && !player.getOffhandItem().isEmpty()) {
                player.displayClientMessage(Component.translatable("message.bsfdcompat.feast.hands_full"), true);
            }
            return InteractionResult.PASS;
        }
        if (remaining <= 0 || !held.isEmpty()) {
            return super.takeServing(level, pos, state, player, hand);
        }
        FoodProperties food = serving.getFoodProperties(player);
        if (food == null || !player.canEat(food.canAlwaysEat()) && !ModEffects.hasAdaptation(player)) {
            return InteractionResult.PASS;
        }
        if (!level.isClientSide) {
            player.eat(level, serving);
            level.setBlock(pos, state.setValue(getServingsProperty(), remaining - 1), Block.UPDATE_ALL);
        }
        return InteractionResult.SUCCESS;
    }

    public ItemStack getCarriedStack(BlockState state) {
        ItemStack stack = new ItemStack(this);
        stack.getOrCreateTagElement("BlockStateTag").putString(getServingsProperty().getName(),
                Integer.toString(state.getValue(getServingsProperty())));
        return stack;
    }
}
