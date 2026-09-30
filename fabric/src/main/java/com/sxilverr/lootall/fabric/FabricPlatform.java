package com.sxilverr.lootall.fabric;

import com.sxilverr.lootall.fabric.mixin.RandomizableContainerAccessor;
import com.sxilverr.lootall.network.LootAllNetwork;
import com.sxilverr.lootall.platform.LootPlatform;
import com.sxilverr.lootall.server.TransferService.ResolvedSink;
import net.fabricmc.fabric.api.transfer.v1.item.ItemStorage;
import net.fabricmc.fabric.api.transfer.v1.item.ItemVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.Storage;
import net.fabricmc.fabric.api.transfer.v1.transaction.Transaction;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;

import java.util.Optional;

public final class FabricPlatform extends LootPlatform {

    @Override
    public boolean hasLootTable(RandomizableContainerBlockEntity be) {
        return ((RandomizableContainerAccessor) be).lootall$getLootTable() != null;
    }

    @Override
    public void giveItem(ServerPlayer player, ItemStack stack) {
        boolean added = player.getInventory().add(stack);
        if (!added || !stack.isEmpty()) {
            ItemEntity drop = player.drop(stack, false);
            if (drop != null) {
                drop.setNoPickUpDelay();
                drop.setTarget(player.getUUID());
            }
        }
    }

    @Override
    public void sendFeedback(ServerPlayer player, Component message, Component transfer) {
        LootAllNetwork.sendFeedback(player, message, Optional.ofNullable(transfer));
    }

    @Override
    public boolean canTargetBlock(ServerLevel level, BlockPos pos) {
        Storage<ItemVariant> storage = blockStorage(level, pos);
        return storage != null && storage.supportsInsertion();
    }

    @Override
    public ResolvedSink blockSink(ServerPlayer player, ServerLevel level, BlockPos pos, boolean chunkReady) {
        if (!chunkReady) {
            return null;
        }
        Storage<ItemVariant> storage = blockStorage(level, pos);
        if (storage == null || !storage.supportsInsertion()) {
            return null;
        }
        return new ResolvedSink(stack -> insert(storage, stack), level.getBlockState(pos).getBlock().getName());
    }

    private static Storage<ItemVariant> blockStorage(ServerLevel level, BlockPos pos) {
        Storage<ItemVariant> storage = ItemStorage.SIDED.find(level, pos, null);
        if (storage != null) {
            return storage;
        }
        for (Direction direction : Direction.values()) {
            storage = ItemStorage.SIDED.find(level, pos, direction);
            if (storage != null) {
                return storage;
            }
        }
        return null;
    }

    private static ItemStack insert(Storage<ItemVariant> storage, ItemStack stack) {
        if (stack.isEmpty()) {
            return stack;
        }
        ItemVariant variant = ItemVariant.of(stack);
        try (Transaction transaction = Transaction.openOuter()) {
            long inserted = storage.insert(variant, stack.getCount(), transaction);
            transaction.commit();
            if (inserted <= 0) {
                return stack;
            }
            if (inserted >= stack.getCount()) {
                return ItemStack.EMPTY;
            }
            ItemStack remainder = stack.copy();
            remainder.setCount(stack.getCount() - (int) inserted);
            return remainder;
        }
    }
}
