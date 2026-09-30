package com.sxilverr.lootall.network;

import com.sxilverr.lootall.Config;
import com.sxilverr.lootall.server.TransferService;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetItemTargetPacket(ResourceLocation item) implements CustomPacketPayload {
    public static final Type<SetItemTargetPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Config.MOD_ID, "set_item_target"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetItemTargetPacket> CODEC =
            StreamCodec.composite(ResourceLocation.STREAM_CODEC, SetItemTargetPacket::item, SetItemTargetPacket::new);

    @Override
    public Type<SetItemTargetPacket> type() {
        return TYPE;
    }

    public static void handle(SetItemTargetPacket msg, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            TransferService.setItemTarget(player, msg.item);
        }
    }
}
