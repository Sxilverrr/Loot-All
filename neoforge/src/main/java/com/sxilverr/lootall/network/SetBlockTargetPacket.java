package com.sxilverr.lootall.network;

import com.sxilverr.lootall.Config;
import com.sxilverr.lootall.server.TransferService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record SetBlockTargetPacket(BlockPos pos) implements CustomPacketPayload {
    public static final Type<SetBlockTargetPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(Config.MOD_ID, "set_block_target"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetBlockTargetPacket> CODEC =
            StreamCodec.composite(BlockPos.STREAM_CODEC, SetBlockTargetPacket::pos, SetBlockTargetPacket::new);

    @Override
    public Type<SetBlockTargetPacket> type() {
        return TYPE;
    }

    public static void handle(SetBlockTargetPacket msg, IPayloadContext ctx) {
        if (ctx.player() instanceof ServerPlayer player) {
            TransferService.setBlockTarget(player, msg.pos);
        }
    }
}
