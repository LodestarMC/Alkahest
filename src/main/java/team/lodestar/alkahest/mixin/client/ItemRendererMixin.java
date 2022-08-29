package team.lodestar.alkahest.mixin.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import team.lodestar.alkahest.core.render.ClientMixinHelper;
import team.lodestar.alkahest.registry.common.ItemRegistration;

@Mixin(ItemRenderer.class)
public class ItemRendererMixin {



    @Inject(method = "Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("TAIL"), cancellable = true)
    public void renderCrushPercent(Font pFr, ItemStack pStack, int pXPosition, int pYPosition, String pText, CallbackInfo ci){
        if(!pStack.is(ItemRegistration.GENERIC_CRUSHED.get())) return;
        if (!pStack.isEmpty()) {
            PoseStack posestack = new PoseStack();
            if(pStack.getCount() != 1) posestack.translate(0.0D, 0.0D, (double)(((ItemRenderer)(Object)this).blitOffset + 200.0F));
            if (pStack.isBarVisible()) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.disableBlend();
                Tesselator tesselator = Tesselator.getInstance();
                BufferBuilder bufferbuilder = tesselator.getBuilder();
                int i = pStack.getBarWidth();
                int j = pStack.getBarColor();
                ClientMixinHelper.fillRect(bufferbuilder, pXPosition + 2, pYPosition + 13, 13, 2, 0, 0, 0, 255);
                ClientMixinHelper.fillRect(bufferbuilder, pXPosition + 2, pYPosition + 13, i, 1, j >> 16 & 255, j >> 8 & 255, j & 255, 255);
                RenderSystem.enableBlend();
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

            if (pStack.getCount() != 1 || pText != null) {
                String s = pText == null ? String.valueOf(pStack.getCount()) : pText;
                MultiBufferSource.BufferSource multibuffersource$buffersource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
                pFr.drawInBatch(s, (float)(pXPosition + 19 - 2 - pFr.width(s)), (float)(pYPosition + 6 + 3), 16777215, true, posestack.last().pose(), multibuffersource$buffersource, false, 0, 15728880);
                multibuffersource$buffersource.endBatch();
            }

            LocalPlayer localplayer = Minecraft.getInstance().player;
            float f = localplayer == null ? 0.0F : localplayer.getCooldowns().getCooldownPercent(pStack.getItem(), Minecraft.getInstance().getFrameTime());
            if (f > 0.0F) {
                RenderSystem.disableDepthTest();
                RenderSystem.disableTexture();
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                Tesselator tesselator1 = Tesselator.getInstance();
                BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                ClientMixinHelper.fillRect(bufferbuilder1, pXPosition, pYPosition + Mth.floor(16.0F * (1.0F - f)), 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                RenderSystem.enableTexture();
                RenderSystem.enableDepthTest();
            }

        }
        ClientMixinHelper.renderGuiItemDecorations(pFr, pStack, pXPosition, pYPosition, pText, (ItemRenderer) (Object) this);
        ci.cancel();
    }
}
