package team.lodestar.alkahest.common.block.mortar;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
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
    private Font font;
    private List<Vec3> passedPoints = List.of(
            Vec3.ZERO
    );
    public MortarRenderer(BlockEntityRendererProvider.Context context){
        this.font = context.getFont();
    }
    @Override
    public void render(MortarBlockEntity pBlockEntity, float pPartialTick, PoseStack ps, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {
        if(!pBlockEntity.inventory.isEmpty()){
            MortarBlockEntity mortar = pBlockEntity;
            ps.pushPose();
            ps.translate(0.5,1.25,0.5);
            ps.scale(0.15f, 0.15f, 0.15f);
            if(mortar.inventory.getStackInSlot(0).is(ItemRegistration.GENERIC_CRUSHED.get())){
                Path path = Path.fromNBT(mortar.inventory.getStackInSlot(0).hasTag() ? (CompoundTag) mortar.inventory.getStackInSlot(0).getTag().get("path") : null);
//                ItemPathData pathData = ItemPathDataListener.ITEM_PATH_DATA.get(Registry.ITEM.get(ResourceLocation.tryParse(mortar.inventory.getStackInSlot(0).getTag().getString("item"))));
//                if(pathData != null){
//                    List<Vec3i> nodes = pathData.nodes;
//                    for(Vec3i node : nodes){
//                        if(IngredientPathUtils.getCurrentVector(path).equals(Vec3.atCenterOf(node))){
//                            if(!passedPoints.contains(IngredientPathUtils.getCurrentVector(path))){
//                                passedPoints.add(IngredientPathUtils.getCurrentVector(path));
//                            }
//                        }
//                    }
//                }
//                if(path != null && !passedPoints.isEmpty()){
//                    PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passedPoints.get(passedPoints.size()-1), IngredientPathUtils.getCurrentVector(path), 0.02f, 255, 255, 255, 255);
//                }
                List<Vec3> passPoints = IngredientPathUtils.passedPoints(path);
                for(int i = 0; i < passPoints.size()-1; i++){
                    ps.pushPose();
                    PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passPoints.get(i), passPoints.get(i+1), 0.02f, 255, 255, 255, 255);
                    ps.popPose();
                }
                if(!passPoints.isEmpty()){
                    PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, passPoints.get(passPoints.size()-1), IngredientPathUtils.getCurrentVector(path), 0.02f, 255, 255, 255, 255);
                } else {
                    PotionMapRenderHelper.drawLineBetween(pBufferSource, ps, Vec3.ZERO, IngredientPathUtils.getCurrentVector(path), 0.02f, 255, 255, 255, 255);
                }
            }
            ps.popPose();
        }
    }
}
