package team.lodestar.alkahest.client.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import team.lodestar.lodestone.helpers.util.Color;

public class PotionMapRenderHelper {
    public static void drawLineBetween(MultiBufferSource buffer, PoseStack mstack, Vec3 local, Vec3 target, float lineWidth, int r, int g, int b, int a) {
        VertexConsumer builder = buffer.getBuffer(RenderType.leash());

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

        mstack.popPose();
    }

    public static void renderNormalCuboid(PoseStack ps, MultiBufferSource buffer, float size, RenderType renderType) {
        ps.pushPose();
        ps.translate((Math.floor(size / 2)), 0, (Math.floor(size / 2)));
        ps.mulPose(Vector3f.XP.rotationDegrees(90));
        VertexConsumer buff = buffer.getBuffer(renderType);
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
        VertexConsumer buff = buffer.getBuffer(renderType);
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
        VertexConsumer buff = buffer.getBuffer(renderType);
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

    public static void renderQuad(PoseStack ps, float size, VertexConsumer buff, Color color) {
        buff.vertex(ps.last().pose(), 0, size, 0).color(color.getRGB()).uv2(0, 1).uv(0, 1).overlayCoords(0, 1).normal(0, 1, 0).endVertex();
        buff.vertex(ps.last().pose(), size, size, 0).color(color.getRGB()).uv2(1, 1).uv(1, 1).overlayCoords(1, 1).normal(1, 1, 0).endVertex();
        buff.vertex(ps.last().pose(), size, 0, 0).color(color.getRGB()).uv2(1, 0).uv(1, 0).overlayCoords(1, 0).normal(1, 0, 0).endVertex();
        buff.vertex(ps.last().pose(), 0, 0, 0).color(color.getRGB()).uv2(0, 0).uv(0, 0).overlayCoords(0, 0).normal(0, 0, 0).endVertex();
    }
}
