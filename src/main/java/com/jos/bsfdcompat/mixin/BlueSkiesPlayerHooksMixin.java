package com.jos.bsfdcompat.mixin;

import com.jos.bsfdcompat.item.BlueKnifeItem;
import com.jos.bsfdcompat.registry.ModEffects;
import com.legacy.blue_skies.asm_hooks.PlayerHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerHooks.class, remap = false)
public abstract class BlueSkiesPlayerHooksMixin {
    @Inject(
            method = "isNerfableTool(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void bsfdcompat$exemptKnives(ItemStack stack, BlockState state, CallbackInfoReturnable<Boolean> cir) {
        if (stack != null && stack.getItem() instanceof BlueKnifeItem) {
            cir.setReturnValue(false);
        }
    }

    @Inject(
            method = "modifyBreakSpeed(FLnet/minecraft/world/level/block/state/BlockState;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/entity/player/Player;)F",
            at = @At(value = "INVOKE",
                    target = "Lcom/legacy/blue_skies/asm_hooks/PlayerHooks;isNerfableTool(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/level/block/state/BlockState;)Z",
                    remap = false),
            cancellable = true, remap = false)
    private static void bsfdcompat$adaptBreakSpeed(float speed, BlockState state, BlockPos pos, Player player,
                                                  CallbackInfoReturnable<Float> cir) {
        // Inject after the native web-harvesting shortcut, preserving its special speed.
        if (ModEffects.hasAdaptation(player)) {
            cir.setReturnValue(speed);
        }
    }

    @Inject(
            method = "isBreakingNerfed(Lnet/minecraft/world/item/Item;Lnet/minecraft/world/entity/player/Player;Lnet/minecraft/world/level/block/state/BlockState;)Z",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void bsfdcompat$adaptHarvesting(Item item, Player player, BlockState state,
                                                  CallbackInfoReturnable<Boolean> cir) {
        if (ModEffects.hasAdaptation(player)) {
            cir.setReturnValue(false);
        }
    }
}
