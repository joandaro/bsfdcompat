package com.jos.bsfdcompat;

import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.legacy.blue_skies.data.BlueSkiesData;
import com.legacy.blue_skies.items.util.ToolUtils;
import net.minecraft.core.NonNullList;
import net.minecraft.core.RegistryAccess;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.ShapedRecipe;
import net.minecraft.world.level.Level;

public final class BlueKnifeRecipe extends ShapedRecipe {
    private final Ingredient blade;

    private BlueKnifeRecipe(ShapedRecipe recipe) {
        super(recipe.getId(), recipe.getGroup(), CraftingBookCategory.EQUIPMENT, 1, 2,
                recipe.getIngredients(), ToolUtils.setStickType(recipe.getResultItem(RegistryAccess.EMPTY).copy(), Items.STICK));
        if (recipe.getRecipeWidth() != 1 || recipe.getRecipeHeight() != 2
                || !(recipe.getResultItem(RegistryAccess.EMPTY).getItem() instanceof BlueKnifeItem)) {
            throw new JsonSyntaxException("A knife_with_handle recipe must be 1x2 and produce a BlueKnifeItem");
        }
        blade = recipe.getIngredients().get(0);
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return KitchenRegistry.KNIFE_WITH_HANDLE.get();
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        // Query the live table, including data-pack additions and changes after /reload.
        // Before Blue Skies syncs its data, show the ordinary stick as its own recipes do.
        var handles = BlueSkiesData.TOOL_HANDLE_TYPES.getData().keySet();
        Item[] sticks = handles.isEmpty() ? new Item[]{Items.STICK} : handles.toArray(Item[]::new);
        return NonNullList.of(Ingredient.EMPTY, blade, Ingredient.of(sticks));
    }

    private Item findHandle(CraftingContainer grid) {
        int width = grid.getWidth();
        int height = grid.getHeight();
        for (int y = 0; y + 1 < height; y++) {
            for (int x = 0; x < width; x++) {
                int materialSlot = x + y * width;
                int handleSlot = materialSlot + width;
                ItemStack handle = grid.getItem(handleSlot);
                if (handle.isEmpty() || !blade.test(grid.getItem(materialSlot))) {
                    continue;
                }
                boolean knownHandle = BlueSkiesData.TOOL_HANDLE_TYPES.hasHandle(handle.getItem())
                        || (BlueSkiesData.TOOL_HANDLE_TYPES.getData().isEmpty() && handle.is(Items.STICK));
                if (!knownHandle) {
                    continue;
                }
                boolean clear = true;
                for (int slot = 0; slot < grid.getContainerSize(); slot++) {
                    if (slot != materialSlot && slot != handleSlot && !grid.getItem(slot).isEmpty()) {
                        clear = false;
                        break;
                    }
                }
                if (clear) {
                    return handle.getItem();
                }
            }
        }
        return null;
    }

    @Override
    public boolean matches(CraftingContainer grid, Level level) {
        return findHandle(grid) != null;
    }

    @Override
    public ItemStack assemble(CraftingContainer grid, RegistryAccess registryAccess) {
        Item handle = findHandle(grid);
        return handle == null ? ItemStack.EMPTY : ToolUtils.setStickType(super.assemble(grid, registryAccess), handle);
    }

    public static final class Serializer implements RecipeSerializer<BlueKnifeRecipe> {
        @Override
        public BlueKnifeRecipe fromJson(ResourceLocation id, JsonObject json) {
            return new BlueKnifeRecipe(RecipeSerializer.SHAPED_RECIPE.fromJson(id, json));
        }

        @Override
        public BlueKnifeRecipe fromNetwork(ResourceLocation id, FriendlyByteBuf buffer) {
            return new BlueKnifeRecipe(RecipeSerializer.SHAPED_RECIPE.fromNetwork(id, buffer));
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, BlueKnifeRecipe recipe) {
            RecipeSerializer.SHAPED_RECIPE.toNetwork(buffer, recipe);
        }
    }
}
