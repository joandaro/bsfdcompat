package com.jos.bsfdcompat;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMenu;

public final class BlueCookingPotMenu extends CookingPotMenu {
    public BlueCookingPotMenu(int windowId, Inventory inventory, BlueCookingPotBlockEntity blockEntity, ContainerData data) {
        // Keep FD's menu type and client factory: its screen and transfer logic work for this subclass.
        super(windowId, inventory, blockEntity, data);
    }

    @Override
    public boolean stillValid(Player player) {
        var pos = blockEntity.getBlockPos();
        return !blockEntity.isRemoved()
                && level.getBlockState(pos).is(KitchenRegistry.BLUE_SKIES_COOKING_POT.get())
                && player.distanceToSqr(pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D) <= 64.0D;
    }
}
