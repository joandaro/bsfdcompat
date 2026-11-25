package com.jos.bsfdcompat.registry;

import com.jos.bsfdcompat.BSFDCompat;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.effect.NourishmentEffect;

public final class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, BSFDCompat.MODID);

    public static final RegistryObject<MobEffect> SKY_ADAPTATION =
            EFFECTS.register("sky_adaptation", SkyAdaptationEffect::new);

    private ModEffects() {
    }

    public static boolean hasAdaptation(Player player) {
        return player != null && player.hasEffect(SKY_ADAPTATION.get());
    }

    private static final class SkyAdaptationEffect extends NourishmentEffect {
        @Override
        public int getColor() {
            return 0x75BEDA;
        }
    }
}
