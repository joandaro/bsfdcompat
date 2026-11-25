package com.jos.bsfdcompat.event;

import com.jos.bsfdcompat.BSFDCompat;
import com.legacy.blue_skies.registries.SkiesBlocks;
import com.legacy.blue_skies.registries.SkiesItems;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Harvests hanging Crescent Fruit while leaving its fruit-bearing leaves intact. */
@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class CrescentFruitHarvestEvents {
    private static final TagKey<Item> KNIVES = ItemTags.create(new ResourceLocation("forge", "tools/knives"));

    private CrescentFruitHarvestEvents() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void harvest(PlayerInteractEvent.RightClickBlock event) {
        if (event.getHand() != InteractionHand.MAIN_HAND || event.getUseBlock() == Event.Result.DENY) {
            return;
        }
        Level level = event.getLevel();
        BlockPos pos = event.getPos();
        if (!level.getBlockState(pos).is(SkiesBlocks.crescent_fruit)) {
            return;
        }
        ItemStack held = event.getItemStack();
        if (!held.isEmpty() && !held.is(KNIVES) && !held.is(SkiesItems.crescent_fruit)) {
            return;
        }
        Player player = event.getEntity();
        if (player.isSpectator() || !player.mayBuild() || !level.mayInteract(player, pos)) {
            return;
        }

        event.setCanceled(true);
        event.setCancellationResult(InteractionResult.sidedSuccess(level.isClientSide));
        if (!level.isClientSide) {
            level.destroyBlock(pos, true, player);
        }
    }
}
