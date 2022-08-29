package team.lodestar.alkahest.core.events;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ColorHandlerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.registry.common.ItemRegistration;

@Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Alkahest.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientEventsModBus {
    @SubscribeEvent
    public static void onRegisterItemColors(final ColorHandlerEvent.Item event){
        event.getItemColors().register(((pStack, pTintIndex) -> {
            if(pTintIndex == 0){
                if(pStack.hasTag()){
                    if(pStack.getTag().contains("color")){
                        return pStack.getTag().getInt("color");
                    }
                }
                return 0xFFFFFF;
            }
            return 0xFFFFFF;
        }), ItemRegistration.GENERIC_CRUSHED.get());
    }
}
