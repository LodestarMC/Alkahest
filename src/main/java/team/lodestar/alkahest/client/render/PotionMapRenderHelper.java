package team.lodestar.alkahest.client.render;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.Mth;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;
import org.lwjgl.opengl.GL11;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.registry.client.RenderTypeRegistry;
import team.lodestar.lodestone.helpers.util.Color;
import team.lodestar.lodestone.setup.LodestoneRenderTypeRegistry;

import static team.lodestar.lodestone.handlers.RenderHandler.DELAYED_RENDER;


public class PotionMapRenderHelper {
    public static void drawLineBetween(MultiBufferSource buffer, PoseStack mstack, Vec3 local, Vec3 target, float lineWidth, int r, int g, int b, int a) {
        VertexConsumer builder = DELAYED_RENDER.getBuffer(RenderType.leash());

        //Calculate yaw
        float rotY = (float) Mth.atan2(target.x - local.x, target.z - local.z);

        //Calculate pitch
        double distX = target.x - local.x;
        double distZ = target.z - local.z;
        float rotX = (float) Mth.atan2(target.y - local.y, Mth.sqrt((float) (distX * distX + distZ * distZ)));

        mstack.pushPose();

        //Translate to start point
        mstack.translate(local.x, local.y, local.z);
        //Rotate to point towards end point
        mstack.mulPose(Vector3f.YP.rotation(rotY));
        mstack.mulPose(Vector3f.XN.rotation(rotX));

        //Calculate distance between points -> length of the line
        float distance = (float) local.distanceTo(target);

        Matrix4f matrix = mstack.last().pose();
        float halfWidth = lineWidth / 2F;

        //Draw horizontal quad
        builder.vertex(matrix, -halfWidth, 0, 0).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder.vertex(matrix, halfWidth, 0, 0).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder.vertex(matrix, halfWidth, 0, distance).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder.vertex(matrix, -halfWidth, 0, distance).color(r, g, b, a).uv2(0xF000F0).endVertex();

        //Draw vertical Quad
        builder.vertex(matrix, 0, -halfWidth, 0).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder.vertex(matrix, 0, halfWidth, 0).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder.vertex(matrix, 0, halfWidth, distance).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder.vertex(matrix, 0, -halfWidth, distance).color(r, g, b, a).uv2(0xF000F0).endVertex();
        builder = DELAYED_RENDER.getBuffer(RenderType.LINES);
        mstack.popPose();
    }

    // TODO: blockpos + (local.scale(0.35f)) and blockpos + (target * 0.35f)
    public static void draw2DLineBetween(MultiBufferSource buff, PoseStack ps, Vec3 local, Vec3 target, float lineWidth, int r, int g, int b, int a, Vec3 blockpos, Vec3 translation){
        ps.pushPose();
        VertexConsumer builder = DELAYED_RENDER.getBuffer(RenderType.lineStrip());
        Matrix4f matrix = ps.last().pose();
        RenderSystem.lineWidth(lineWidth);
        Vec3 center = blockpos.add(0.5, 1.5, 0.5);
        double localActualDistance = Minecraft.getInstance().player.getEyePosition().distanceTo(center);
        float localDistanceFactor = (float) Math.max(0, Math.min(10, (localActualDistance)));
        builder.vertex(matrix, (float) local.x, (float) local.y, (float) local.z).color(255f/r, 255f/g, 255f/b, 1).uv(0,1).overlayCoords(0).uv2(15728640).normal(0,0,0).endVertex();
        builder.vertex(matrix, (float) target.x, (float) target.y, (float) target.z).color(255f/r, 255f/g, 255f/b, 1).uv(0,1).overlayCoords(0).uv2(15728640).normal(0,0,0).endVertex();
        builder = DELAYED_RENDER.getBuffer(RenderType.LINES);
        ps.popPose();
    }

    public static void renderNormalCuboid(PoseStack ps, MultiBufferSource buffer, float size, RenderType renderType) {
        ps.pushPose();
        ps.translate((Math.floor(size / 2)), 0, (Math.floor(size / 2)));
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        VertexConsumer buff = DELAYED_RENDER.getBuffer(renderType);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, size, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, size, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, size, 0);
        renderQuad(ps, -size, buff);
        ps.mulPose(Vector3f.YP.rotationDegrees(270));
        ps.translate(size, 0, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.YP.rotationDegrees(180));
        ps.translate(size, 0, -size);
        renderQuad(ps, size, buff);
        ps.popPose();
    }

    public static void renderQuad(PoseStack ps, float size, VertexConsumer buff) {
        buff.vertex(ps.last().pose(), 0, size, 0).color(0xFFFF55FF).uv(0, 1).endVertex();
        buff.vertex(ps.last().pose(), size, size, 0).color(0xFFFF55FF).uv(1, 1).endVertex();
        buff.vertex(ps.last().pose(), size, 0, 0).color(0xFFFF55FF).uv(1, 0).endVertex();
        buff.vertex(ps.last().pose(), 0, 0, 0).color(0xFFFF55FF).uv(0, 0).endVertex();
    }

    public static void renderInvertedCube(PoseStack ps, MultiBufferSource buffer, float size, RenderType renderType) {
        ps.pushPose();
        ps.translate(-(Math.floor(size / 2)), 0, -(Math.floor(size / 2)));
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        VertexConsumer buff = DELAYED_RENDER.getBuffer(renderType);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, -size, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, -size, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, -size, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.YP.rotationDegrees(270));
        ps.translate(-size, 0, 0);
        renderQuad(ps, size, buff);
        ps.mulPose(Vector3f.YP.rotationDegrees(180));
        ps.translate(-size, 0, size);
        renderQuad(ps, size, buff);
        ps.popPose();
    }

    public static void renderInvertedCube(PoseStack ps, MultiBufferSource buffer, float size, RenderType renderType, Color color) {
        ps.pushPose();
        ps.translate(-size / 2, 0, -size / 2);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        VertexConsumer buff = DELAYED_RENDER.getBuffer(renderType);
        renderQuad(ps, size, buff, color);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, -size, 0);
        renderQuad(ps, size, buff, color);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, -size, 0);
        renderQuad(ps, size, buff, color);
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        ps.translate(0, -size, 0);
        renderQuad(ps, size, buff, color);
        ps.mulPose(Vector3f.YP.rotationDegrees(270));
        ps.translate(-size, 0, 0);
        renderQuad(ps, size, buff, color);
        ps.mulPose(Vector3f.YP.rotationDegrees(180));
        ps.translate(-size, 0, size);
        renderQuad(ps, size, buff, color);
        ps.popPose();
    }

    /**
     * Renders a billboarded translucent magenta square. Useful for preventing insanity.
     */
    public static void renderTest(PoseStack stack, MultiBufferSource buffer) {

        RenderType type = LodestoneRenderTypeRegistry.TRANSPARENT_SOLID;
        VertexConsumer builder = buffer.getBuffer(type);
        Vector4f center = new Vector4f(0, 0, 0, 1);
        center.transform(stack.last().pose());
        Matrix3f normal = stack.last().normal();
        float xp = center.x() + 0.5F;
        float xn = center.x() - 0.5F;
        float yp = center.y() + 0.5F;
        float yn = center.y() - 0.5F;
        float z = center.z();
        int r = 255;
        int g = 0;
        int b = 255;
        int a = 128;
        int packedLight = 0x00F000F0;
        builder.vertex(xp, yp, z).color(r, g, b, a).uv(0, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(xn, yp, z).color(r, g, b, a).uv(0, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(xn, yn, z).color(r, g, b, a).uv(1, 1).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
        builder.vertex(xp, yn, z).color(r, g, b, a).uv(1, 0).overlayCoords(OverlayTexture.NO_OVERLAY).uv2(packedLight).normal(normal, 0, 1, 0).endVertex();
    }

    public static void renderQuad(PoseStack ps, float size, VertexConsumer buff, Color color) {
        buff.vertex(ps.last().pose(), 0, size, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 1).uv2(0, 1).overlayCoords(0, 1).normal(0, 1, 0).endVertex();
        buff.vertex(ps.last().pose(), size, size, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1, 1).uv2(1, 1).overlayCoords(1, 1).normal(1, 1, 0).endVertex();
        buff.vertex(ps.last().pose(), size, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(1, 0).uv2(1, 0).overlayCoords(1, 0).normal(1, 0, 0).endVertex();
        buff.vertex(ps.last().pose(), 0, 0, 0).color(color.getRed(), color.getGreen(), color.getBlue(), color.getAlpha()).uv(0, 0).uv2(0, 0).overlayCoords(0, 0).normal(0, 0, 0).endVertex();
    }

    public static void translateToFacing(BlockEntity pBlockEntity, PoseStack ps, float pPartialTick, float offsetX, float offsetY, float offsetZ) {
        ps.mulPose(Vector3f.ZP.rotationDegrees(180));
        ps.translate(-0.25,0,-0.5);
        LocalPlayer player = Minecraft.getInstance().player;
        Vec3 eyeVec = player.getEyePosition(pPartialTick);
        double oldEyeY = player.yOld + player.getEyeHeight();
        Vec3 playerVec = new Vec3(player.xOld + (eyeVec.x - player.xOld) * pPartialTick, oldEyeY + (eyeVec.y - oldEyeY) * pPartialTick + 1.5, player.zOld + (eyeVec.z - player.zOld) * pPartialTick);
        Vec3 center = new Vec3(pBlockEntity.getBlockPos().getX() + offsetX, pBlockEntity.getBlockPos().getY() + offsetY, pBlockEntity.getBlockPos().getZ() + offsetZ);

        Vec3 startYaw = new Vec3(0.0, 0.0, 1.0);
        Vec3 endYaw = new Vec3(playerVec.x, 0.0, playerVec.z).subtract(new Vec3(center.x, 0.0, center.z)).normalize();
        Vec3 d = playerVec.subtract(center);

        // Find angle between start & end in yaw
        float yaw = (float) Math.toDegrees(Math.atan2(endYaw.x - startYaw.x, endYaw.z - startYaw.z)) + 90;

        // Find angle between start & end in pitch
        float pitch = Mth.clamp((float) Math.toDegrees(Math.atan2(Math.sqrt(d.z * d.z + d.x * d.x), d.y) + Math.PI), -90, -90);

        Quaternion Q = Quaternion.ONE.copy();

        // doubling to account for how quats work
        Q.mul(new Quaternion(new Vector3f(0.0f, 1.0f, 0.0f), -yaw * 2, true));
//        Q.mul(new Quaternion(new Vector3f(1.0f, 0.0f, 0.0f), pitch + 90, true));
        //Q.mul(-1);
        ps.mulPose(Q);
    }
}
