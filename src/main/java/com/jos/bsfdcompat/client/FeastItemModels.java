package com.jos.bsfdcompat.client;

import com.jos.bsfdcompat.BSFDCompat;
import com.jos.bsfdcompat.FeastRegistry;
import com.jos.bsfdcompat.PortableFeastBlock;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

/** Selects the existing block model using the whole dish's saved portion count. */
@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class FeastItemModels {
    private static final ResourceLocation SERVINGS = new ResourceLocation(BSFDCompat.MODID, "servings");

    private FeastItemModels() {
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            register(FeastRegistry.STARDUST_SHEPHERDS_PIE_FEAST.get());
            register(FeastRegistry.COMET_HONEY_GLAZED_MONITOR_TAIL_FEAST.get());
        });
    }

    private static void register(Item item) {
        // ItemProperties clamps values to 0..1; quarters keep all four servings distinct.
        ItemProperties.register(item, SERVINGS,
                (stack, level, entity, seed) -> remainingFraction(stack));
    }

    private static float remainingFraction(ItemStack stack) {
        PortableFeastBlock feast = (PortableFeastBlock) ((BlockItem) stack.getItem()).getBlock();
        int remaining = feast.getMaxServings();
        CompoundTag state = stack.getTagElement("BlockStateTag");
        if (state != null) {
            remaining = feast.getServingsProperty()
                    .getValue(state.getString(feast.getServingsProperty().getName())).orElse(remaining);
        }
        return remaining / (float) feast.getMaxServings();
    }
}
