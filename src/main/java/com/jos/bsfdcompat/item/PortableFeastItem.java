package com.jos.bsfdcompat.item;

import java.util.List;
import javax.annotation.Nullable;

import com.jos.bsfdcompat.block.PortableFeastBlock;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

public final class PortableFeastItem extends BlockItem {
    public PortableFeastItem(PortableFeastBlock block, Properties properties) {
        super(block, properties);
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                 List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        PortableFeastBlock feast = (PortableFeastBlock) getBlock();
        int remaining = feast.getMaxServings();
        CompoundTag state = stack.getTagElement("BlockStateTag");
        if (state != null) {
            remaining = feast.getServingsProperty()
                    .getValue(state.getString(feast.getServingsProperty().getName())).orElse(remaining);
        }
        tooltip.add(Component.translatable("tooltip.bsfdcompat.feast.servings", remaining,
                feast.getMaxServings()).withStyle(ChatFormatting.GRAY));
        tooltip.add(Component.translatable("tooltip.bsfdcompat.feast.controls").withStyle(ChatFormatting.DARK_GRAY));
        tooltip.add(Component.translatable("tooltip.bsfdcompat.feast.bowl").withStyle(ChatFormatting.DARK_GRAY));
    }
}
