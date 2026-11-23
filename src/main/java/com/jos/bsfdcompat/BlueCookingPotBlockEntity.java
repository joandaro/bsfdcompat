package com.jos.bsfdcompat;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import vectorwing.farmersdelight.common.block.entity.CookingPotBlockEntity;
import vectorwing.farmersdelight.common.crafting.CookingPotRecipe;

public final class BlueCookingPotBlockEntity extends CookingPotBlockEntity {
    private boolean bonusProgressPending;

    public BlueCookingPotBlockEntity(BlockPos pos, BlockState state) {
        super(pos, state);
    }

    @Override
    public BlockEntityType<?> getType() {
        // FD 1.3.4 has no constructor accepting another block entity type.
        // Use our registered type for persistence, networking and block-state validation.
        return KitchenRegistry.COOKING_POT_BLOCK_ENTITY.get();
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, BlueCookingPotBlockEntity pot) {
        pot.bonusProgressPending = true;
        try {
            CookingPotBlockEntity.cookingTick(level, pos, state, pot);
        } finally {
            pot.bonusProgressPending = false;
        }
    }

    @Override
    protected boolean canCook(CookingPotRecipe recipe) {
        boolean canCook = super.canCook(recipe);
        // FD 1.3.4 calls this once after checking heat, immediately before incrementing cookTime.
        // ContainerData index 0 is the real cookTime field; only add one extra unit here.
        // Do not tick the whole machine twice: that would also double container/output handling.
        if (bonusProgressPending && canCook && recipe.getId().getNamespace().equals(BSFDCompat.MODID)) {
            cookingPotData.set(0, cookingPotData.get(0) + 1);
        }
        bonusProgressPending = false;
        return canCook;
    }

    @Override
    public Component getName() {
        Component customName = getCustomName();
        return customName != null ? customName : Component.translatable("container.bsfdcompat.blue_skies_cooking_pot");
    }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player) {
        return new BlueCookingPotMenu(windowId, inventory, this, cookingPotData);
    }
}
