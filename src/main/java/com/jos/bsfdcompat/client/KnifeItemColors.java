package com.jos.bsfdcompat.client;

import com.jos.bsfdcompat.BSFDCompat;
import com.jos.bsfdcompat.registry.KitchenRegistry;
import com.legacy.blue_skies.data.BlueSkiesData;
import com.legacy.blue_skies.data.objects.ToolHandleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/** Tints the shared handle layer using Blue Skies' live tool-handle data. */
@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class KnifeItemColors {
    private KnifeItemColors() {
    }

    @SubscribeEvent
    public static void onRegisterItemColors(RegisterColorHandlersEvent.Item event) {
        event.register((stack, tintIndex) -> tintIndex == 0 ? getHandleColor(stack) : -1,
                KitchenRegistry.AQUITE_KNIFE.get(),
                KitchenRegistry.PYROPE_KNIFE.get(),
                KitchenRegistry.DIOPSIDE_KNIFE.get(),
                KitchenRegistry.CHAROITE_KNIFE.get(),
                KitchenRegistry.HORIZONITE_KNIFE.get());
    }

    private static int getHandleColor(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String value = tag == null ? "" : tag.getString("Stick");
        ResourceLocation stick = value.isEmpty() ? null : ResourceLocation.tryParse(value);
        ToolHandleType handle = stick == null
                ? BlueSkiesData.TOOL_HANDLE_TYPES.getHandle(Items.STICK)
                : BlueSkiesData.TOOL_HANDLE_TYPES.getHandle(stick);
        return handle.getColor();
    }
}
