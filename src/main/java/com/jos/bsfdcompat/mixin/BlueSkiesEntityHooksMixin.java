package com.jos.bsfdcompat.mixin;

import com.jos.bsfdcompat.registry.ModEffects;
import com.legacy.blue_skies.entities.util.SkiesEntityHooks;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SkiesEntityHooks.class, remap = false)
public abstract class BlueSkiesEntityHooksMixin {
    @Inject(method = "nerfDamage(Lnet/minecraft/world/damagesource/DamageSource;F)F",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void bsfdcompat$adaptMelee(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        if (source.getDirectEntity() instanceof Player player && ModEffects.hasAdaptation(player)) {
            cir.setReturnValue(damage);
        }
    }

    @Inject(method = "nerfIndirectDamage(Lnet/minecraft/world/damagesource/DamageSource;F)F",
            at = @At("HEAD"), cancellable = true, remap = false)
    private static void bsfdcompat$adaptRanged(DamageSource source, float damage, CallbackInfoReturnable<Float> cir) {
        // The causing entity is the player; the direct entity is normally the projectile.
        if (source.getEntity() instanceof Player player && ModEffects.hasAdaptation(player)) {
            cir.setReturnValue(damage);
        }
    }
}
