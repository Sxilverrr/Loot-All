package com.sxilverr.lootall.network;

import com.sxilverr.lootall.server.TransferService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.17 {
import net.minecraftforge.network.NetworkEvent;
//?} else {
/*import net.minecraftforge.fml.network.NetworkEvent;*/
//?}

import java.util.function.Supplier;

public class SetBlockTargetPacket {
    private final BlockPos pos;

    public SetBlockTargetPacket(BlockPos pos) {
        this.pos = pos;
    }

    public static void encode(SetBlockTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeBlockPos(msg.pos);
    }

    public static SetBlockTargetPacket decode(FriendlyByteBuf buf) {
        return new SetBlockTargetPacket(buf.readBlockPos());
    }

    public static void handle(SetBlockTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                TransferService.setBlockTarget(player, msg.pos);
            }
        });
        context.setPacketHandled(true);
    }
}
