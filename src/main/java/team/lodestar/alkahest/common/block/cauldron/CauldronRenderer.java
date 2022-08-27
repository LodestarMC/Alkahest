package team.lodestar.alkahest.common.block.cauldron;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.phys.Vec3;
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

import java.util.ArrayList;
import java.util.List;

public class CauldronRenderer implements BlockEntityRenderer<CauldronBlockEntity> {

    public CauldronRenderer(BlockEntityRendererProvider.Context context) {
    }

    @Override
    public void render(CauldronBlockEntity pBlockEntity, float pPartialTick, PoseStack ps, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        ps.pushPose();
        ps.translate(0.5, 1.25f, 0.5);
        ps.scale(0.15f, 0.15f, 0.15f);
        for (PotionPathData data : PotionPathDataListener.potionMap.getMap()) {
            ps.pushPose();
            ps.translate(data.location.x, data.location.y, data.location.z);
            ItemStack potion = Registry.ITEM.get(ResourceLocation.tryParse("minecraft:potion")).getDefaultInstance();
            PotionUtils.setCustomEffects(potion, data.getEffects());
            PotionMapRenderHelper.renderInvertedCube(ps, pBufferSource, data.radius / 2.5f, RenderType.lightning(), data.getColor());
            ps.popPose();
        }
        List<Vec3> passedPoints = new ArrayList<>();
        ps.pushPose();
        if (pBlockEntity.inventory.getStackInSlot(0).is(ItemRegistration.GENERIC_CRUSHED.get())) {
            Path path = pBlockEntity.path;
            List<Vec3> passPoints = IngredientPathUtils.pathPoints(path);
            for (int i = 0; i < passPoints.size() - 1; i++) {
                ps.pushPose();
                PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passPoints.get(i), passPoints.get(i + 1), 0.02f, 255, 255, 255, 255);
                ps.popPose();
            }
            if (!passPoints.isEmpty()) {
                ps.pushPose();
                PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passPoints.get(passPoints.size() - 1), IngredientPathUtils.getCurrentVector(path), 0.02f, 255, 255, 255, 255);
                ps.popPose();
            }
        }
        ps.popPose();
        ps.popPose();
    }
}
