package team.lodestar.alkahest.registry.common;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.common.block.mortar.MortarBlockEntity;
import team.lodestar.alkahest.common.block.mortar.MortarRenderer;

public class BlockEntityRegistration {
    public static DeferredRegister<BlockEntityType<?>> BLOCK_ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, Alkahest.MODID);
    public static final RegistryObject<BlockEntityType<MortarBlockEntity>> MORTAR = BLOCK_ENTITY_TYPES.register("mortar", () -> BlockEntityType.Builder.of(MortarBlockEntity::new, BlockRegistration.MORTAR.get()).build(null));

    public static void register() {

    }

    @Mod.EventBusSubscriber(modid = Alkahest.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ClientOnly{
        @SubscribeEvent
        public static void registerBlockEntityRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(MORTAR.get(), MortarRenderer::new);
        }
    }
}
