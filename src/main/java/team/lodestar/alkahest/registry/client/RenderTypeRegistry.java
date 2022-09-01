package team.lodestar.alkahest.registry.client;


import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import net.minecraft.Util;
import net.minecraft.client.renderer.RenderStateShard;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import team.lodestar.alkahest.Alkahest;

import java.io.IOException;
import java.util.function.Function;

import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR;
import static com.mojang.blaze3d.vertex.DefaultVertexFormat.POSITION_COLOR_TEX;

public class RenderTypeRegistry {

    public static RenderType additiveTexture(ResourceLocation texture){
        return AlkahestRenderTypes.ADDITIVE_TEXTURE.apply(texture);
    }

    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Alkahest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ShaderRegistryEvent{
        @SubscribeEvent
        public static void shaderRegistry(RegisterShadersEvent event) throws IOException {
            event.registerShader(new ShaderInstance(event.getResourceManager(), Alkahest.alkahest("rendertype_additive_texture"), POSITION_COLOR), shaderInstance -> {
                AlkahestRenderTypes.additiveTexture = shaderInstance;
            });
        }
    }

    public static class AlkahestRenderTypes extends RenderType {

        private static ShaderInstance additiveTexture;

        private static final ShaderStateShard RENDER_TYPE_ADDITIVE_TEXTURE = new ShaderStateShard(() -> additiveTexture);

        private AlkahestRenderTypes(String s, VertexFormat v, VertexFormat.Mode m, int i, boolean b, boolean b2, Runnable r, Runnable r2) {
            super(s, v, m, i, b, b2, r, r2);
            throw new IllegalStateException("This class is not meant to be constructed!");
        }

        public static Function<ResourceLocation, RenderType> ADDITIVE_TEXTURE = Util.memoize(AlkahestRenderTypes::additiveTexture);
        private static RenderType additiveTexture(ResourceLocation texture){
            RenderType.CompositeState rendertype$state = RenderType.CompositeState.builder()
                    .setShaderState(RENDER_TYPE_ADDITIVE_TEXTURE)
                    .setTextureState(NO_TEXTURE)
                    .setTransparencyState(TRANSLUCENT_TRANSPARENCY)
                    .setLightmapState(LIGHTMAP)
                    .setOverlayState(NO_OVERLAY)
                    .createCompositeState(true);
            return create("additive_texture", POSITION_COLOR, VertexFormat.Mode.QUADS, 256, true, false, rendertype$state);
        }
    }
}
