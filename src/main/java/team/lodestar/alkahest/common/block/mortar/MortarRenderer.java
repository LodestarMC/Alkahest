package team.lodestar.alkahest.common.block.mortar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import team.lodestar.alkahest.client.render.PotionMapRenderHelper;
import team.lodestar.alkahest.core.listeners.ItemPathDataListener;
import team.lodestar.alkahest.core.path.IngredientPathUtils;
import team.lodestar.alkahest.core.path.ItemPathData;
import team.lodestar.alkahest.core.path.Path;
import team.lodestar.alkahest.registry.common.ItemRegistration;

import java.util.ArrayList;
import java.util.List;

public class MortarRenderer implements BlockEntityRenderer<MortarBlockEntity> {
    Font font;
    public List<Vec3> passPoints;
    public MortarRenderer(BlockEntityRendererProvider.Context context){
        this.font = context.getFont();
         passPoints = new ArrayList<>();
    }
    @Override
    public void render(MortarBlockEntity pBlockEntity, float pPartialTick, PoseStack ps, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if(!pBlockEntity.inventory.isEmpty()){
            MortarBlockEntity mortar = pBlockEntity;
            ps.pushPose();
            ps.translate(0.5,1.35,0.5);
            ps.scale(0.75f,0.75f,0.75f);
            if(mortar.inventory.getStackInSlot(0).is(ItemRegistration.GENERIC_CRUSHED.get())){
                Path path = Path.fromNBT(mortar.inventory.getStackInSlot(0).hasTag() ? (CompoundTag) mortar.inventory.getStackInSlot(0).getTag().get("path") : null);
                renderItem(mortar.inventory.getStackInSlot(0), ps, pBufferSource);
                drawPath(ps, pBufferSource, path);
            } else {
                Path path = ItemPathDataListener.getPathForItem(mortar.inventory.getStackInSlot(0));
                if(path != null){
                    renderItem(mortar.inventory.getStackInSlot(0), ps, pBufferSource);
                    drawPath(ps, pBufferSource, path);
                }
            }
            ps.popPose();
        }
    }

    public void drawPath(PoseStack ps, MultiBufferSource pBufferSource, Path path) {
        passPoints.clear();
        IngredientPathUtils.addPathToNodeList(passPoints, path);
        if (!passPoints.isEmpty()) {
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

    public static void renderItem(ItemStack stack, PoseStack ps, MultiBufferSource buff){
        ps.pushPose();
        ps.scale(0.5f,0.5f,0.5f);
//        ps.translate(0.34f,-0.1f,-0.05f);
        Minecraft.getInstance().getItemRenderer().renderStatic(stack, ItemTransforms.TransformType.GROUND, 15728640, OverlayTexture.NO_OVERLAY, ps, buff, 1);
        ps.popPose();
    }
}
