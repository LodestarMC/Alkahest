package team.lodestar.alkahest.core.events;

import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import team.lodestar.alkahest.core.listeners.ItemPathDataListener;
import team.lodestar.alkahest.core.listeners.PotionPathDataListener;

@Mod.EventBusSubscriber
public class RuntimeEvents {
    @SubscribeEvent
    public static void registerListeners(AddReloadListenerEvent event){
        ItemPathDataListener.register(event);
        PotionPathDataListener.register(event);
    }
}
