package team.lodestar.alkahest.core.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.path.Path;
import team.lodestar.lodestone.helpers.util.Color;

import javax.annotation.Nullable;

public class ClientMixinHelper {
    public static void renderGuiItemDecorations(Font pFr, ItemStack pStack, int pXPosition, int pYPosition, @Nullable String pText, ItemRenderer renderer) {
        if (!pStack.isEmpty()) {
            PoseStack posestack = new PoseStack();
            if (pStack.hasTag()) {
                if (pStack.getTag().contains("path")) {
                    Path path = Path.fromNBT((CompoundTag) pStack.getTag().get("path"));
                    Color mainCol = new Color(pStack.getTag().getInt("middleColor")).mixWith(new Color(225, 225, 225), 0.25f);
                    Color color = ColorHelper.darker(ColorHelper.darker(mainCol));
                    Tesselator tesselator = Tesselator.getInstance();
                    BufferBuilder bufferbuilder = tesselator.getBuilder();
                    posestack.translate(0.0D, 0.0D, renderer.blitOffset + 200.0F);
                    posestack.pushPose();
                    posestack.scale(0.5f, 0.5f, 0.5f);
                    //fillRect(bufferbuilder, pXPosition, pYPosition, 5, 5, color.getRed(), color.getGreen(), color.getBlue(), 255);
                    drawRect(bufferbuilder, pXPosition+0.75f, pYPosition+0.5f, 4, 4, color.getRed(), color.getGreen(), color.getBlue(), 255, getGrindTexture(path.getFlatProgress()));
                    drawRect(bufferbuilder, pXPosition, pYPosition, 4, 4, mainCol.getRed(), mainCol.getGreen(), mainCol.getBlue(), 255, getGrindTexture(path.getFlatProgress()));
//                    pFr.drawInBatch(s, (float) (pXPosition + 16 - (pFr.width(s) / 2)) * 2, (float) (pYPosition + 1) * 2, 16777215, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
                    posestack.popPose();
                }
            }
        }
    }

    public static void fillRect(BufferBuilder pRenderer, int pX, int pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue, int pAlpha) {
        RenderSystem.setShader(GameRenderer::getPositionColorShader);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
        pRenderer.vertex((double)(pX + 0), (double)(pY + 0), 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex((double)(pX + 0), (double)(pY + pHeight), 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex((double)(pX + pWidth), (double)(pY + pHeight), 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.vertex((double)(pX + pWidth), (double)(pY + 0), 0.0D).color(pRed, pGreen, pBlue, pAlpha).endVertex();
        pRenderer.end();
        BufferUploader.end(pRenderer);
    }

    public static void drawRect(BufferBuilder pRenderer, float pX, float pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue, int pAlpha, ResourceLocation texture) {
        RenderSystem.setShader(GameRenderer::getPositionColorTexShader);
        RenderSystem.setShaderTexture(0, texture);
        pRenderer.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_COLOR_TEX);
        pRenderer.vertex((pX), (pY), 0.0D).color(pRed, pGreen, pBlue, pAlpha).uv(0, 0).endVertex();
        pRenderer.vertex((pX), (pY + pHeight), 0.0D).color(pRed, pGreen, pBlue, pAlpha).uv(0, 1).endVertex();
        pRenderer.vertex((pX + pWidth), (pY + pHeight), 0.0D).color(pRed, pGreen, pBlue, pAlpha).uv(1, 1).endVertex();
        pRenderer.vertex((pX + pWidth), (pY), 0.0D).color(pRed, pGreen, pBlue, pAlpha).uv(1, 0).endVertex();
        pRenderer.end();
        BufferUploader.end(pRenderer);
    }

    public static ResourceLocation getGrindTexture(float percent){
        if(percent >= 100){
            return Alkahest.alkahest("textures/gui/grind/grind4.png");
        } else if(percent >= 75){
            return Alkahest.alkahest("textures/gui/grind/grind3.png");
        } else if(percent >= 50){
            return Alkahest.alkahest("textures/gui/grind/grind2.png");
        } else if(percent >= 25){
            return Alkahest.alkahest("textures/gui/grind/grind1.png");
        } else {
            return Alkahest.alkahest("textures/gui/grind/grind0.png");
        }
    }
}
