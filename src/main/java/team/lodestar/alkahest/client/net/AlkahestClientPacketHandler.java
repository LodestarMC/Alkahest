package team.lodestar.alkahest.client.net;

import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkEvent;
import team.lodestar.alkahest.common.block.cauldron.CauldronBlockEntity;
import team.lodestar.alkahest.core.net.ClientboundCauldronPacket;

import java.util.function.Supplier;

public class AlkahestClientPacketHandler {
    public static void handleClientCauldronPacket(ClientboundCauldronPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            if (Minecraft.getInstance().level.getBlockEntity(packet.pos) instanceof CauldronBlockEntity cbe) {
                cbe.paths = packet.paths;
                cbe.containedPotionNames = packet.potionNames;
            }
        });
    }
}
