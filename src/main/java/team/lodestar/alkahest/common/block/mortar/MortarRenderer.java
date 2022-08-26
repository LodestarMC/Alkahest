package team.lodestar.alkahest.common.block.mortar;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;

public class MortarRenderer implements BlockEntityRenderer<MortarBlockEntity> {
    public MortarRenderer(BlockEntityRendererProvider.Context context){
    }
    @Override
    public void render(MortarBlockEntity pBlockEntity, float pPartialTick, PoseStack pPoseStack, MultiBufferSource pBufferSource, int pPackedLight, int pPackedOverlay) {

    }
}
