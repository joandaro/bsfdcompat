package com.jos.bsfdcompat;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.legacy.blue_skies.data.BlueSkiesData;
import com.legacy.blue_skies.data.objects.ToolHandleType;
import com.legacy.blue_skies.data.objects.tags.SkiesItemTags;
import com.legacy.blue_skies.items.util.ICustomHandleItem;
import com.legacy.blue_skies.items.util.ToolUtils;
import com.legacy.blue_skies.util.StringUtil;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import vectorwing.farmersdelight.common.item.KnifeItem;

import java.util.List;

public final class BlueKnifeItem extends KnifeItem implements ICustomHandleItem {
    private final float attackSpeedModifier;

    public BlueKnifeItem(Tier tier, float damageModifier, float attackSpeedModifier, Properties properties) {
        super(tier, damageModifier, attackSpeedModifier, properties);
        this.attackSpeedModifier = attackSpeedModifier;
    }

    private ToolHandleType getHandle(ItemStack stack) {
        CompoundTag tag = stack.getTag();
        String value = tag == null ? "" : tag.getString("Stick");
        ResourceLocation stick = value.isEmpty() ? null : ResourceLocation.tryParse(value);
        // Old items and /give stacks have no Stick tag. Reading their attributes must not mutate NBT.
        return stick == null
                ? BlueSkiesData.TOOL_HANDLE_TYPES.getHandle(Items.STICK)
                : BlueSkiesData.TOOL_HANDLE_TYPES.getHandle(stick);
    }

    @Override
    public int getMaxDamage(ItemStack stack) {
        return Math.round(getTier().getUses() * getHandle(stack).getDurabilityModifier());
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getAttributeModifiers(EquipmentSlot slot, ItemStack stack) {
        Multimap<Attribute, AttributeModifier> original = super.getAttributeModifiers(slot, stack);
        if (slot != EquipmentSlot.MAINHAND) {
            return original;
        }

        Multimap<Attribute, AttributeModifier> modified = HashMultimap.create(original);
        modified.get(Attributes.ATTACK_SPEED).removeIf(modifier -> modifier.getId().equals(BASE_ATTACK_SPEED_UUID));
        modified.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Weapon modifier",
                attackSpeedModifier * getHandle(stack).getAttackSpeedModifier(), AttributeModifier.Operation.ADDITION));
        return modified;
    }

    @Override
    public ItemStack getDefaultInstance() {
        return ToolUtils.setStickType(super.getDefaultInstance(), Items.STICK);
    }

    @Override
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        super.appendHoverText(stack, level, tooltip, flag);
        if (stack.is(SkiesItemTags.HORIZONITE_TOOLS)) {
            tooltip.add(Component.literal(StringUtil.getAbilityText("gui.blue_skies.item.ability.fire")));
        }
    }
}
