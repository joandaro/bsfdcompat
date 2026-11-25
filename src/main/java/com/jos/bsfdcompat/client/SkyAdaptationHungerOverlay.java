package com.jos.bsfdcompat.client;

import com.jos.bsfdcompat.BSFDCompat;
import com.jos.bsfdcompat.registry.ModEffects;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.GuiOverlayManager;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;

import java.util.Random;

/** Sky-blue hunger outlines for Sky Adaptation, registered only on the client. */
@Mod.EventBusSubscriber(modid = BSFDCompat.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class SkyAdaptationHungerOverlay {
    private static final ResourceLocation FOOD_LEVEL_ELEMENT = new ResourceLocation("minecraft", "food_level");
    private static final int SKY_BLUE = 0x87CEFA;

    // Drumstick silhouette adapted from Farmer's Delight's MIT-licensed HUD icons.
    private static final int[] FOOD_SHAPE = {
            0x00C, 0x01E, 0x03F, 0x07F, 0x07E, 0x07C, 0x1F8, 0x1C0, 0x0C0
    };
    private static final int[] OUTLINE = createOutline();
    private static final int[] HALO = createHalo();
    private static final Random HUNGER_JITTER = new Random();

    private SkyAdaptationHungerOverlay() {
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onRenderGuiOverlayPost(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != GuiOverlayManager.findOverlay(FOOD_LEVEL_ELEMENT)) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null || minecraft.options.hideGui || !ModEffects.hasAdaptation(player)
                || player.getVehicle() instanceof LivingEntity) {
            return;
        }
        if (!(minecraft.gui instanceof ForgeGui gui) || !gui.shouldDrawSurvivalElements()) {
            return;
        }

        FoodData foodData = player.getFoodData();
        int foodLevel = Mth.clamp(foodData.getFoodLevel(), 0, 20);
        int ticks = gui.getGuiTicks();
        int right = minecraft.getWindow().getGuiScaledWidth() / 2 + 91;
        int top = minecraft.getWindow().getGuiScaledHeight() - gui.rightHeight + 10;
        float pulse = 0.82F + 0.18F * Mth.sin((ticks + event.getPartialTick()) * 0.12F);
        int glow = color(Math.round(255.0F * pulse));
        int halo = color(Math.round(48.0F * pulse));

        HUNGER_JITTER.setSeed((long) (ticks * 312871));
        GuiGraphics graphics = event.getGuiGraphics();
        graphics.flush();
        Matrix4f matrix = graphics.pose().last().pose();
        VertexConsumer vertices = graphics.bufferSource().getBuffer(RenderType.gui());
        for (int icon = 0; icon < 10; icon++) {
            int x = right - icon * 8 - 9;
            int y = top;
            if (foodData.getSaturationLevel() <= 0.0F && ticks % (foodLevel * 3 + 1) == 0) {
                y += HUNGER_JITTER.nextInt(3) - 1;
            }

            int foodPoints = Mth.clamp(foodLevel - icon * 2, 0, 2);
            if (foodPoints > 0) {
                drawMask(vertices, matrix, HALO, 11, x - 1, y - 1, 0, halo);
            }
            drawMask(vertices, matrix, OUTLINE, 9, x, y, 0, color(75));
            if (foodPoints > 0) {
                drawMask(vertices, matrix, OUTLINE, 9, x, y, foodPoints == 1 ? 4 : 0, glow);
            }
        }
        graphics.flush();
    }

    private static int color(int alpha) {
        return (Mth.clamp(alpha, 0, 255) << 24) | SKY_BLUE;
    }

    private static boolean inShape(int x, int y) {
        return x >= 0 && x < 9 && y >= 0 && y < FOOD_SHAPE.length
                && (FOOD_SHAPE[y] & (1 << x)) != 0;
    }

    private static int[] createOutline() {
        int[] outline = new int[9];
        for (int y = 0; y < 9; y++) {
            for (int x = 0; x < 9; x++) {
                if (inShape(x, y) && (!inShape(x - 1, y) || !inShape(x + 1, y)
                        || !inShape(x, y - 1) || !inShape(x, y + 1))) {
                    outline[y] |= 1 << x;
                }
            }
        }
        return outline;
    }

    private static int[] createHalo() {
        int[] halo = new int[11];
        for (int y = -1; y <= 9; y++) {
            for (int x = -1; x <= 9; x++) {
                if (!inShape(x, y) && (inShape(x - 1, y) || inShape(x + 1, y)
                        || inShape(x, y - 1) || inShape(x, y + 1))) {
                    halo[y + 1] |= 1 << (x + 1);
                }
            }
        }
        return halo;
    }

    private static void drawMask(VertexConsumer vertices, Matrix4f matrix, int[] mask, int width,
                                 int x, int y, int fromX, int color) {
        int alpha = (color >>> 24) & 255;
        int red = (color >>> 16) & 255;
        int green = (color >>> 8) & 255;
        int blue = color & 255;
        for (int row = 0; row < mask.length; row++) {
            int column = fromX;
            while (column < width) {
                if ((mask[row] & (1 << column)) == 0) {
                    column++;
                    continue;
                }
                int start = column;
                do {
                    column++;
                } while (column < width && (mask[row] & (1 << column)) != 0);
                float left = x + start;
                float right = x + column;
                float top = y + row;
                float bottom = top + 1;
                vertices.vertex(matrix, left, top, 0.0F).color(red, green, blue, alpha).endVertex();
                vertices.vertex(matrix, left, bottom, 0.0F).color(red, green, blue, alpha).endVertex();
                vertices.vertex(matrix, right, bottom, 0.0F).color(red, green, blue, alpha).endVertex();
                vertices.vertex(matrix, right, top, 0.0F).color(red, green, blue, alpha).endVertex();
            }
        }
    }
}
