package com.jos.bsfdcompat.client;

import com.jos.bsfdcompat.BSFDCompat;
import com.jos.bsfdcompat.registry.SoilRegistry;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Uses the carried block-state tag to select the matching soil item model. */
@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class SoilItemModels {
    private static final ResourceLocation VARIANT = new ResourceLocation(BSFDCompat.MODID, "soil_variant");

    private SoilItemModels() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> SoilRegistry.ITEMS.getEntries().forEach(item ->
                ItemProperties.register(item.get(), VARIANT,
                        (stack, level, entity, seed) -> SoilRegistry.isEverdawn(stack) ? 1.0F : 0.0F)));
    }
}
