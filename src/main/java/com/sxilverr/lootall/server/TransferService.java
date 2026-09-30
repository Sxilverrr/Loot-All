package com.sxilverr.lootall.server;

import com.sxilverr.lootall.Compat;
import com.sxilverr.lootall.Text;
import com.sxilverr.lootall.config.LootConfig;
import com.sxilverr.lootall.core.TransferData;
import com.sxilverr.lootall.platform.LootPlatform;
import net.minecraft.core.BlockPos;
//? if >=1.20 {
import net.minecraft.core.registries.BuiltInRegistries;
//?} else {
/*import net.minecraft.core.Registry;*/
//?}
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public final class TransferService {

    public interface LootSink {
        ItemStack insert(ItemStack stack);
    }

    public static final class ResolvedSink {
        private final LootSink sink;
        private final Component name;
        private final Runnable onComplete;

        public ResolvedSink(LootSink sink, Component name, Runnable onComplete) {
            this.sink = sink;
            this.name = name;
            this.onComplete = onComplete;
        }

        public ResolvedSink(LootSink sink, Component name) {
            this(sink, name, null);
        }

        public LootSink sink() {
            return sink;
        }

        public Component name() {
            return name;
        }

        public Runnable onComplete() {
            return onComplete;
        }
    }

    private TransferService() {
    }

    public static ResolvedSink resolveSink(ServerPlayer player) {
        LootPlatform platform = LootPlatform.INSTANCE;
        if (!LootConfig.enableLootingTransfer || !platform.canTransfer(player)) {
            return null;
        }
        MinecraftServer server = player.getServer();
        if (server == null) {
            return null;
        }
        TransferData.Target target = TransferData.get(server).getTarget(player.getUUID());
        if (target instanceof TransferData.ItemTarget) {
            return platform.itemSink(player, item(((TransferData.ItemTarget) target).item()));
        }
        if (!(target instanceof TransferData.BlockTarget)) {
            return null;
        }
        TransferData.BlockTarget block = (TransferData.BlockTarget) target;
        ServerLevel targetLevel = server.getLevel(block.dimension());
        if (targetLevel == null) {
            return null;
        }
        boolean sameDimension = targetLevel.dimension() == Compat.level(player).dimension();
        if (LootConfig.transferRequireSameDimension && !sameDimension) {
            return null;
        }
        BlockPos pos = block.pos();
        if (sameDimension && LootConfig.maxLootTransferDistance > 0) {
            double maxSq = (double) LootConfig.maxLootTransferDistance * LootConfig.maxLootTransferDistance;
            if (player.distanceToSqr(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) > maxSq) {
                return null;
            }
        }
        ChunkPos chunkPos = new ChunkPos(pos);
        boolean chunkReady = !LootConfig.transferRequireLoadedChunk
                || targetLevel.getChunkSource().getChunkNow(chunkPos.x, chunkPos.z) != null;
        return platform.blockSink(player, targetLevel, pos, chunkReady);
    }

    public static void setBlockTarget(ServerPlayer player, BlockPos pos) {
        MinecraftServer server = player.getServer();
        if (server == null || !allowed(player)) {
            return;
        }
        Level level = Compat.level(player);
        if (!LootPlatform.INSTANCE.canTargetBlock((ServerLevel) level, pos)) {
            player.displayClientMessage(Text.translatable("message.lootall.target_invalid"), true);
            return;
        }
        TransferData.get(server).setBlockTarget(player.getUUID(), level.dimension(), pos);
        Component name = level.getBlockState(pos).getBlock().getName();
        player.displayClientMessage(Text.translatable(
                "message.lootall.target_set", name, pos.getX(), pos.getY(), pos.getZ()), true);
    }

    public static void setItemTarget(ServerPlayer player, ResourceLocation id) {
        MinecraftServer server = player.getServer();
        if (server == null || !allowed(player)) {
            return;
        }
        Item item = item(id);
        if (!LootPlatform.INSTANCE.canTargetItem(player, item)) {
            player.displayClientMessage(Text.translatable("message.lootall.item_target_invalid"), true);
            return;
        }
        TransferData.get(server).setItemTarget(player.getUUID(), id);
        Component name = new ItemStack(item).getHoverName();
        player.displayClientMessage(Text.translatable("message.lootall.target_set_item", name), true);
    }

    public static void clearTarget(ServerPlayer player) {
        MinecraftServer server = player.getServer();
        if (server == null) {
            return;
        }
        TransferData.get(server).clear(player.getUUID());
        player.displayClientMessage(Text.translatable("message.lootall.target_cleared"), true);
    }

    private static boolean allowed(ServerPlayer player) {
        if (LootPlatform.INSTANCE.canTransfer(player)) {
            return true;
        }
        player.displayClientMessage(Text.translatable("message.lootall.no_stage"), true);
        return false;
    }

    private static Item item(ResourceLocation id) {
        //? if >=1.20 {
        return BuiltInRegistries.ITEM.get(id);
        //?} else {
        /*return Registry.ITEM.get(id);*/
        //?}
    }
}
