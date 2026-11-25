package com.jos.bsfdcompat.event;

import com.jos.bsfdcompat.BSFDCompat;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.common.block.FeastBlock;

@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class FeastInteractions {
    private FeastInteractions() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void pickUpFeast(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.START
                && event.getAction() != PlayerInteractEvent.LeftClickBlock.Action.CLIENT_HOLD) {
            return;
        }
        Player player = event.getEntity();
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!player.getMainHandItem().isEmpty() || !player.mayBuild() || player.isSpectator()
                || !player.canReach(pos, 1.5D) || !level.mayInteract(player, pos)) {
            return;
        }
        BlockState state = level.getBlockState(pos);
        if (!(state.getBlock() instanceof FeastBlock feast)
                || state.getValue(feast.getServingsProperty()) <= 0 || state.hasBlockEntity()) {
            return;
        }
        // The carried state is restored by normal BlockItem placement.
        if (!(feast.asItem() instanceof BlockItem blockItem) || blockItem.getBlock() != feast) {
            return;
        }

        // Forge still sends the START packet when client-side mining is canceled.
        // Cancel on both sides for prediction; only the server transfers the actual item.
        event.setCanceled(true);
        if (!level.isClientSide && event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.START) {
            ItemStack carried = new ItemStack(blockItem);
            carried.getOrCreateTagElement("BlockStateTag").putString(feast.getServingsProperty().getName(),
                    Integer.toString(state.getValue(feast.getServingsProperty())));
            if (level.removeBlock(pos, false)) {
                player.setItemInHand(InteractionHand.MAIN_HAND, carried);
                level.playSound(null, pos, SoundEvents.ITEM_PICKUP, SoundSource.PLAYERS, 0.4F, 1.0F);
            }
        }
    }
}
