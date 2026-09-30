package com.sxilverr.lootall.platform;

import com.sxilverr.lootall.server.TransferService;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

public abstract class LootPlatform {
    public static LootPlatform INSTANCE;

    public abstract boolean hasLootTable(RandomizableContainerBlockEntity be);

    public boolean hasLootTable(AbstractMinecartContainer cart) {
        //? if >=1.19 {
        return cart.getLootTable() != null;
        //?} else {
        /*return false;*/
        //?}
    }

    public boolean isLootr(BlockEntity be) {
        return false;
    }

    public boolean isLootr(AbstractMinecartContainer cart) {
        return false;
    }

    public int lootLootr(ServerPlayer player, BlockEntity be) {
        return -1;
    }

    public int lootLootr(ServerPlayer player, AbstractMinecartContainer cart) {
        return -1;
    }

    public abstract void giveItem(ServerPlayer player, ItemStack stack);

    public abstract void sendFeedback(ServerPlayer player, Component message, Component transfer);

    public boolean canAutoLoot(ServerPlayer player) {
        return true;
    }

    public boolean canTransfer(ServerPlayer player) {
        return true;
    }

    public abstract boolean canTargetBlock(ServerLevel level, BlockPos pos);

    public abstract TransferService.ResolvedSink blockSink(ServerPlayer player, ServerLevel level, BlockPos pos, boolean chunkReady);

    public boolean canTargetItem(ServerPlayer player, Item item) {
        return false;
    }

    public TransferService.ResolvedSink itemSink(ServerPlayer player, Item item) {
        return null;
    }
}
