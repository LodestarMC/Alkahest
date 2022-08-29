package team.lodestar.alkahest.common.block.cauldron;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.registries.ForgeRegistries;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.client.render.PotionMapRenderHelper;
import team.lodestar.alkahest.core.listeners.ItemPathDataListener;
import team.lodestar.alkahest.core.listeners.PotionPathDataListener;
import team.lodestar.alkahest.core.path.IngredientPathUtils;
import team.lodestar.alkahest.core.path.Path;
import team.lodestar.alkahest.core.path.PotionPathData;
import team.lodestar.alkahest.registry.common.ItemRegistration;
import team.lodestar.lodestone.helpers.util.Color;
import team.lodestar.lodestone.setup.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {
    Font font;
    List<Vec3> passPoints;
    List<MobEffectInstance> passEffects;
    RenderType renderType = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.apply(Alkahest.alkahest("textures/vfx/white.png"));
    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
        passPoints = new ArrayList<>();
        passEffects = new ArrayList<>();
    }

    // TODO: Adjust potion area rendering to be fully accurate, smooth move along path when adding new item to mix.

    @Override
    public void render(CauldronBlockEntity pBlockEntity, float pPartialTick, PoseStack ps, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ps.pushPose();
        ps.pushPose();
        ps.translate(0.5, 1.5f, 0.5);
        ps.scale(0.35f, 0.35f, 0.35f);
        float speed = 0.025f;
        float movementY = (float)Math.cos((pBlockEntity.getLevel().getGameTime()+pPartialTick)*speed) * 0.05f;
        float movementX = (float)Math.sin((pBlockEntity.getLevel().getGameTime()+pPartialTick)*speed) * 0.05f;
        float movementZ = (float)Math.sin(-(pBlockEntity.getLevel().getGameTime()+pPartialTick)*speed) * 0.05f;
        ps.translate(movementX, movementY, movementZ);
        if (!pBlockEntity.paths.isEmpty()) {
            List<Path> path = pBlockEntity.paths;
            passPoints.clear();
            for (Path p : path) {
                IngredientPathUtils.addPathToNodeList(passPoints, p);
            }
            renderPotion(pBlockEntity, ps, pBufferSource);
            if (passPoints.size() > 1) {
                Vec3 currentPos = passPoints.get(passPoints.size()-1);
                ps.translate(-currentPos.x, -currentPos.y, -currentPos.z);
                ps.pushPose();
                PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, Vec3.ZERO, passPoints.get(0), 0.02f, 255, 255, 255, 255);
                ps.popPose();
            }
            for (int i = 0; i < passPoints.size() - 1; i++) {
                ps.pushPose();
                PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passPoints.get(i), passPoints.get(i + 1), 0.02f, 255, 255, 255, 255);
                ps.popPose();
            }
        }
        for (PotionPathData data : PotionPathDataListener.potionMap.getMap()) {
            ps.pushPose();
            ps.translate(data.location.x - (data.radius/2f), data.location.y - (data.radius/2f), data.location.z - (data.radius/2f));
//            ItemStack potion = Registry.ITEM.get(ResourceLocation.tryParse("minecraft:potion")).getDefaultInstance();
//            PotionUtils.setCustomEffects(potion, data.getEffects());
            PotionMapRenderHelper.renderInvertedCube(ps, pBufferSource, data.radius, renderType, data.getColor());
            ps.popPose();
        }
        ps.popPose();
        for (int i = pBlockEntity.containedPotionNames.size(); i > 0; i--) {
            ps.pushPose();
            ps.mulPose(Vector3f.YP.rotationDegrees(180f));
            ps.translate(-0.75f, 1.5f, 0);
            PotionMapRenderHelper.translateToFacing(pBlockEntity, ps, pPartialTick, 0.5f, 2f, 0.5f);
            ps.translate(0, 0, -0.75f);
            ps.scale(0.002f, 0.002f, 0.002f);
            ps.translate(-font.width(pBlockEntity.containedPotionNames.get(i - 1)) / 2f, 50 - i * 10f, 0);
            font.draw(ps, pBlockEntity.containedPotionNames.get(i - 1), 1, 1, 0xFFFFFFFF);
            ps.translate(0,0,0.001f);
            font.draw(ps, pBlockEntity.containedPotionNames.get(i - 1), 2, 2, 0xFF000000);
//            renderText(ps, font, pBlockEntity.containedPotionNames.get(i-1));
            ps.popPose();
        }
        ps.popPose();
    }
    public static void renderText(PoseStack ps, Font font, String text) {
        ps.pushPose();
        ps.mulPose(Vector3f.ZP.rotationDegrees(180f));
        ps.scale(0.01f, 0.01f, 0.01f);
        ps.translate(-font.width(text) / 2f, 0, 0);
        font.draw(ps, text, 1, 1, 0xFFFFFF);
        ps.popPose();
    }

    public void renderPotion(CauldronBlockEntity be, PoseStack ps, MultiBufferSource buff){
        ps.pushPose();
        ps.scale(1f,1f,1f);
        ps.translate(0,0.15f,0);
        ItemStack stack = Items.POTION.getDefaultInstance();
        passEffects.clear();
        for(PotionPathData data : be.containedPotions){
            passEffects.addAll(data.effects);
        }
        PotionUtils.setCustomEffects(stack, passEffects);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.FIXED, 15728640, OverlayTexture.NO_OVERLAY, ps, buff, 1);
        ps.popPose();
    }
}
