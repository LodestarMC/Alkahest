package team.lodestar.alkahest.core.net;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import team.lodestar.alkahest.client.net.AlkahestClientPacketHandler;
import team.lodestar.alkahest.common.block.cauldron.CauldronBlockEntity;
import team.lodestar.alkahest.core.path.Path;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class ClientboundCauldronPacket {
    public final BlockPos pos;
    public final List<Path> paths;
    public final List<String> potionNames;
    public final int pathlength;
    public final int potionlength;

    public ClientboundCauldronPacket(BlockPos pos, List<Path> paths, List<String> potionNames) {
        this.pos = pos;
        this.paths = paths;
        this.pathlength = paths.size();
        this.potionNames = potionNames;
        this.potionlength = potionNames.size();
    }

    public static void encode(ClientboundCauldronPacket packet, FriendlyByteBuf buf){
        buf.writeBlockPos(packet.pos);
        buf.writeVarInt(packet.pathlength);
        for(int i = 0; i < packet.paths.size(); i++){
            buf.writeNbt(packet.paths.get(i).toNBT());
        }
        buf.writeVarInt(packet.potionlength);
        for(int i = 0; i < packet.potionNames.size(); i++){
            buf.writeUtf(packet.potionNames.get(i));
        }
    }

    public static ClientboundCauldronPacket decode(FriendlyByteBuf buf){
        BlockPos pos = buf.readBlockPos();
        List<Path> path = new ArrayList<>();
        int length = buf.readVarInt();
        for(int i = 0; i < length; i++){
            path.add(Path.fromNBT(buf.readNbt()));
        }
        List<String> potionNames = new ArrayList<>();
        int potionlength = buf.readVarInt();
        for(int i = 0; i < potionlength; i++){
            potionNames.add(buf.readUtf());
        }
        return new ClientboundCauldronPacket(pos, path, potionNames);
    }

    public static void handle(ClientboundCauldronPacket packet, Supplier<NetworkEvent.Context> ctx){
        ctx.get().enqueueWork(() -> {
            AlkahestClientPacketHandler.handleClientCauldronPacket(packet, ctx);
        });
        ctx.get().setPacketHandled(true);
    }
}
