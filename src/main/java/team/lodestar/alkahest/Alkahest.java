package team.lodestar.alkahest;

import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.InterModComms;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.event.lifecycle.InterModProcessEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import team.lodestar.alkahest.core.alchemy.PotionMapInstructions;
import team.lodestar.alkahest.core.handlers.AlkahestPacketHandler;

import java.util.stream.Collectors;

import static team.lodestar.alkahest.registry.common.BlockEntityRegistration.BLOCK_ENTITY_TYPES;
import static team.lodestar.alkahest.registry.common.BlockRegistration.BLOCKS;
import static team.lodestar.alkahest.registry.common.ItemRegistration.ITEMS;
import static team.lodestar.alkahest.registry.common.RecipeRegistration.RECIPE_SERIALIZERS;

// The value here should match an entry in the META-INF/mods.toml file
@Mod("alkahest")
public class Alkahest {

    // Directly reference a slf4j logger
    public static final String MODID = "alkahest";
    public static final Logger LOGGER = LogUtils.getLogger();

    public Alkahest() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::setup);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::enqueueIMC);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::processIMC);
        RECIPE_SERIALIZERS.register(modEventBus);
        BLOCK_ENTITY_TYPES.register(modEventBus);
        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        PotionMapInstructions.setup();
        AlkahestPacketHandler.init();
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void setup(final FMLCommonSetupEvent event) {
        LOGGER.info("Alkahest Loading");
    }

    private void enqueueIMC(final InterModEnqueueEvent event) {
        InterModComms.sendTo("alkahest", "helloworld", () -> {
            LOGGER.info("Hello world from the MDK");
            return "Hello world";
        });
    }

    private void processIMC(final InterModProcessEvent event) {
        LOGGER.info("Got IMC {}", event.getIMCStream().
                map(m -> m.messageSupplier().get()).
                collect(Collectors.toList()));
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("Alkahest Server Starting");
    }

    // You can use EventBusSubscriber to automatically subscribe events on the contained class (this is subscribing to the MOD
    // Event bus for receiving Registry Events)
    @Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class RegistryEvents {
    }

    public static ResourceLocation alkahest(String name) {
        return new ResourceLocation(MODID, name);
    }
}
