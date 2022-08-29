package team.lodestar.alkahest.core.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.registry.common.ItemRegistration;
import team.lodestar.lodestone.helpers.util.Color;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Alkahest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsModBus {
    @SubscribeEvent
    public static void onRegisterItemColors(final ColorHandlerEvent.Item event){
        event.getItemColors().register(((pStack, pTintIndex) -> {
                if(pStack.hasTag()){
                    if(pStack.getTag().contains("lightestColor")){
                        return switch (pTintIndex) {
                            case 0 -> pStack.getTag().getInt("lightestColor");
                            case 1 -> new Color(pStack.getTag().getInt("lightestColor")).mixWith(new Color(pStack.getTag().getInt("middleColor")), 0.5f).getRGB();
                            case 2 -> new Color(pStack.getTag().getInt("middleColor")).getRGB();
                            case 3 -> new Color(pStack.getTag().getInt("middleColor")).mixWith(new Color(pStack.getTag().getInt("darkestColor")), 0.5f).getRGB();
                            case 4 -> pStack.getTag().getInt("darkestColor");
                            default -> 0xFFFFFFFF;
                        };
                    }
                }
                return 0xFFFFFF;
        }), ItemRegistration.GENERIC_CRUSHED.get());
    }
}
