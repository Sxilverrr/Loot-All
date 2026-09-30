package com.sxilverr.lootall.network;

import com.sxilverr.lootall.server.TransferService;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
//? if >=1.17 {
import net.minecraftforge.network.NetworkEvent;
//?} else {
/*import net.minecraftforge.fml.network.NetworkEvent;*/
//?}

import java.util.function.Supplier;

public class SetItemTargetPacket {
    private final ResourceLocation item;

    public SetItemTargetPacket(ResourceLocation item) {
        this.item = item;
    }

    public static void encode(SetItemTargetPacket msg, FriendlyByteBuf buf) {
        buf.writeResourceLocation(msg.item);
    }

    public static SetItemTargetPacket decode(FriendlyByteBuf buf) {
        return new SetItemTargetPacket(buf.readResourceLocation());
    }

    public static void handle(SetItemTargetPacket msg, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                TransferService.setItemTarget(player, msg.item);
            }
        });
        context.setPacketHandled(true);
    }
}
