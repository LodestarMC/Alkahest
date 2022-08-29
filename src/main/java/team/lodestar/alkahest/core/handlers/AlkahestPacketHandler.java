package team.lodestar.alkahest.core.handlers;

import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import team.lodestar.alkahest.Alkahest;
import team.lodestar.alkahest.core.net.ClientboundCauldronPacket;

public class AlkahestPacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            Alkahest.alkahest("alkahest"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int ID = 0;

    public static void init() {
        Alkahest.LOGGER.info("Registering packets");
        INSTANCE.registerMessage(ID++, ClientboundCauldronPacket.class, ClientboundCauldronPacket::encode, ClientboundCauldronPacket::decode, ClientboundCauldronPacket::handle);
    }
}
