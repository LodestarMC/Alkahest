package team.lodestar.alkahest.common.block.cauldron;

import net.minecraft.client.resources.model.Material;
import net.minecraftforge.client.ForgeHooksClient;
import team.lodestar.lodestone.helpers.util.Color;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.ForgeRenderTypes;
import net.minecraftforge.fluids.FluidAttributes;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.client.render.PotionMapRenderHelper;
import team.lodestar.alkahest.core.listeners.ItemPathDataListener;
import team.lodestar.alkahest.core.listeners.PotionPathDataListener;
import team.lodestar.alkahest.core.path.IngredientPathUtils;
import team.lodestar.alkahest.core.path.Path;
import team.lodestar.alkahest.core.path.PotionPathData;
import team.lodestar.alkahest.core.render.ColorHelper;
import team.lodestar.alkahest.registry.client.RenderTypeRegistry;
import team.lodestar.alkahest.registry.common.ItemRegistration;
import team.lodestar.lodestone.setup.LodestoneRenderTypeRegistry;
import team.lodestar.lodestone.systems.rendering.VFXBuilders;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static team.lodestar.lodestone.handlers.RenderHandler.DELAYED_RENDER;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {
    Font font;
    List<Vec3> passPoints = new ArrayList<>();
    List<MobEffectInstance> passEffects = new ArrayList<>();
    public static RenderType FLUID_TYPE = LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyAndCache(InventoryMenu.BLOCK_ATLAS);
    RenderType renderType = LodestoneRenderTypeRegistry.ADDITIVE_TEXTURE.apply(Alkahest.alkahest("textures/vfx/white.png"));

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
        font = context.getFont();
    }

    // TODO: Adjust potion area rendering to be fully accurate, smooth move along path when adding new item to mix.

    @Override
    public void render(CauldronBlockEntity pBlockEntity, float pPartialTick, PoseStack ps, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ps.pushPose();
        ps.pushPose();

        ps.pushPose();
        FluidStack fluidStack = pBlockEntity.tank.getFluid();
        if(pBlockEntity.tank.isEmpty()){
            ps.popPose();
            ps.popPose();
            ps.popPose();
            return;
        }
        if(!pBlockEntity.tank.isEmpty()) {
            VertexConsumer cons = DELAYED_RENDER.getBuffer(FLUID_TYPE);
            ps.translate(0, Math.min(1, 0.25f + pBlockEntity.tank.getFluidAmount() / 1400f), 0);
            Fluid fluid = fluidStack.getFluid();
            TextureAtlasSprite still = ForgeHooksClient.getFluidSprites(pBlockEntity.getLevel(), pBlockEntity.getBlockPos(), fluid.defaultFluidState())[0];
            Color color = ColorHelper.getColor(fluid.getAttributes().getColor(fluidStack));
            for(PotionPathData data : pBlockEntity.containedPotions) {
                color = color.mixWith(new Color(PotionUtils.getColor(data.getEffects())).setAlpha(255), 0.75f);
            }
            Matrix4f matrix = ps.last().pose();
            cons.vertex(matrix, 0, 0, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(still.getU0(), still.getV1()).uv2(0, 1).endVertex();
            cons.vertex(matrix, 1, 0, 1).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(still.getU1(), still.getV1()).uv2(1, 1).endVertex();
            cons.vertex(matrix, 1, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(still.getU1(), still.getV0()).uv2(1, 0).endVertex();
            cons.vertex(matrix, 0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(still.getU0(), still.getV0()).uv2(0, 0).endVertex();
        }
        ps.popPose();

        renderGrid(ps, pBufferSource, pBlockEntity);
        ps.translate(0.5, 1.5f, 0.5);
        ps.scale(0.35f, 0.35f, 0.35f);
        if(pBlockEntity.expanded) ps.translate(0, -pBlockEntity.mutablePotionMap.getLowestPoint().y + 0.5, 0);
        float speed = 0.025f;
        float movementY = (float)Math.cos((pBlockEntity.getLevel().getGameTime()+pPartialTick)*speed) * 0.05f;
        float movementX = (float)Math.sin((pBlockEntity.getLevel().getGameTime()+pPartialTick)*speed) * 0.05f;
        float movementZ = (float)Math.sin(-(pBlockEntity.getLevel().getGameTime()+pPartialTick)*speed) * 0.05f;
        ps.translate(movementX, movementY, movementZ);
        if (!pBlockEntity.paths.isEmpty()) {
            List<Path> path = pBlockEntity.paths;
            passPoints.clear();
            passEffects.clear();
            for (Path p : path) {
                IngredientPathUtils.addPathToNodeList(passPoints, p);
            }
            renderPotion(pBlockEntity, ps, pBufferSource);
            if (passPoints.size() > 1) {
                Vec3 currentPos = passPoints.get(passPoints.size()-1);
                ps.translate(-currentPos.x, -currentPos.y, -currentPos.z);
                if(!(passPoints.get(passPoints.size()-1).distanceTo(Vec3.ZERO) > pBlockEntity.visionArea)){
                    ps.pushPose();
                    PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, Vec3.ZERO, passPoints.get(0), 0.02f, 255, 255, 255, 255);
                    ps.popPose();
                }
            }
            for (int i = 0; i < passPoints.size() - 1; i++) {
                ps.pushPose();
                if(passPoints.get(passPoints.size()-1).distanceTo(passPoints.get(i)) > pBlockEntity.visionArea){
                    ps.popPose();
                    continue;
                }
                PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passPoints.get(i), passPoints.get(i + 1), 0.02f, 255, 255, 255, 255);
                ps.popPose();
            }
        }
        for (PotionPathData data : pBlockEntity.mutablePotionMap.getMap()) {
            ps.pushPose();
            ps.translate(data.location.x - (data.radius/2f), data.location.y - (data.radius/2f), data.location.z - (data.radius/2f));
            Vec3 posVec = Vec3.ZERO.add(data.location.x - (data.radius/2f), data.location.y - (data.radius/2f), data.location.z - (data.radius/2f));
            if(passPoints.get(passPoints.size()-1).distanceTo(posVec) > pBlockEntity.visionArea){
                ps.popPose();
                continue;
            }
//            ItemStack potion = Registry.ITEM.get(ResourceLocation.tryParse("minecraft:potion")).getDefaultInstance();
//            PotionUtils.setCustomEffects(potion, data.getEffects());
            PotionMapRenderHelper.renderInvertedCube(ps, pBufferSource, data.radius, renderType, data.getColor());
            String did = data.effects.get(0).effect.getDescriptionId().replaceFirst("(?:\\.)+", "=");
            String rl = did.substring(did.indexOf("=")+1).replaceAll("\\.", ":");
            String effect = rl.trim().split(":")[1];
            String modID = rl.trim().split(":")[0];
            RenderType test = LodestoneRenderTypeRegistry.TRANSPARENT_TEXTURE.applyAndCache(ResourceLocation.tryParse(modID + ":textures/mob_effect/" + effect + ".png"));
            ps.mulPose(Vector3f.ZN.rotationDegrees(180f));
            ps.translate(-data.radius/3f, -data.radius/1.125f, 0);
            ps.scale(data.radius*0.75f, data.radius*0.75f, data.radius*0.75f);
            PotionMapRenderHelper.renderQuad(ps, 1, DELAYED_RENDER.getBuffer(test), team.lodestar.lodestone.helpers.util.Color.WHITE);
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
    public static void renderGrid(PoseStack ps, MultiBufferSource buff, CauldronBlockEntity pBlockEntity){
        ps.pushPose();
        ps.translate(0,1f,0);
        //ps.mulPose(Vector3f.YP.rotationDegrees(90f));
        Vec3[] corners = new Vec3[]{
                new Vec3(0,0,1),
                new Vec3(1,0,1),
                new Vec3(1,0,0),
                new Vec3(0,0,0)
        };
        ps.translate(0,0,1);
        for (int i = 0; i < corners.length; i++) {
            int next = (i+1)%corners.length;
            ps.pushPose();
            ps.mulPose(Vector3f.YP.rotationDegrees(90f));
            PotionMapRenderHelper.drawLineBetween(buff, ps, corners[i], corners[next], 0.002f, 255, 255, 255, 255);
//            ps.mulPose(Vector3f.YP.rotationDegrees(-180f*i));
            drawLineGrid(ps, buff, corners[i], i, pBlockEntity);
            ps.popPose();
        }
//        drawLineGrid(ps, buff);
//        ps.translate(0,0,1);
//        ps.mulPose(Vector3f.YP.rotationDegrees(90f));
//        drawLineGrid(ps, buff);
//        ps.translate(1,0,0);
////        ps.mulPose(Vector3f.YP.rotationDegrees(90f));
//        drawLineGrid(ps, buff);
//        ps.translate(-1,0,1);
//        ps.mulPose(Vector3f.YP.rotationDegrees(90f));
//        drawLineGrid(ps, buff);
        ps.popPose();
    }

    public static void drawLineGrid(PoseStack ps, MultiBufferSource buff, Vec3 start, int r, CauldronBlockEntity pBlockEntity) {
        ps.pushPose();
        ps.translate(start.x, start.y, start.z);
        ps.mulPose(Vector3f.YP.rotationDegrees(90*(r+1)));
        PotionMapRenderHelper.draw2DLineBetween(buff, ps, Vec3.ZERO, new Vec3(0,1,0), 0.002f, 255, 255, 255, 127, Vec3.atLowerCornerOf(pBlockEntity.getBlockPos()), Vec3.ZERO);
        for(int i = 0; i < 10; i++){
            ps.pushPose();
            ps.translate(0,0,0.1f*i);
            PotionMapRenderHelper.draw2DLineBetween(buff, ps, Vec3.ZERO, new Vec3(0,0.05f,0), 0.002f, 255, 255, 255, 127, Vec3.atLowerCornerOf(pBlockEntity.getBlockPos()), Vec3.ZERO.add(0,0,0.1f*i));
            ps.popPose();
        }
        for (int i = 0; i < 10; i++) {
            ps.pushPose();
            ps.translate(0,0.1f*i,0);
            PotionMapRenderHelper.draw2DLineBetween(buff, ps, Vec3.ZERO, new Vec3(0,0,0.05f), 0.002f, 255, 255, 255, 127, Vec3.atLowerCornerOf(pBlockEntity.getBlockPos()), Vec3.ZERO.add(0,0.1f*i,0));
            ps.mulPose(Vector3f.ZP.rotationDegrees(-90f));
            PotionMapRenderHelper.draw2DLineBetween(buff, ps, Vec3.ZERO, new Vec3(0,0.05f,0), 0.002f, 255, 255, 255, 127, Vec3.atLowerCornerOf(pBlockEntity.getBlockPos()), Vec3.ZERO.add(0,0.1f*i,0));
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
