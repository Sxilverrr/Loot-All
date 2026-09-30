package com.sxilverr.lootall.neoforge;

import com.sxilverr.lootall.Compat;
import com.sxilverr.lootall.Text;
import com.sxilverr.lootall.compat.AppliedEnergisticsCompat;
import com.sxilverr.lootall.compat.CuriosCompat;
import com.sxilverr.lootall.compat.LootrCompat;
import com.sxilverr.lootall.compat.MekanismCompat;
import com.sxilverr.lootall.compat.PrettyPipesCompat;
import com.sxilverr.lootall.compat.ProjectECompat;
import com.sxilverr.lootall.compat.RefinedStorageCompat;
import com.sxilverr.lootall.compat.SimpleStorageNetworkCompat;
import com.sxilverr.lootall.compat.TomsStorageCompat;
import com.sxilverr.lootall.network.LootFeedbackPacket;
import com.sxilverr.lootall.platform.LootPlatform;
import com.sxilverr.lootall.server.TransferService.LootSink;
import com.sxilverr.lootall.server.TransferService.ResolvedSink;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.vehicle.AbstractMinecartContainer;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.RandomizableContainerBlockEntity;
import net.minecraft.world.level.Level;
import net.neoforged.fml.ModList;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemHandlerHelper;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Optional;

public final class NeoForgePlatform extends LootPlatform {
    private static final boolean LOOTR = ModList.get().isLoaded("lootr");
    private static final boolean RS = ModList.get().isLoaded("refinedstorage");
    private static final boolean PE = ModList.get().isLoaded("projecte");
    private static final boolean AE2 = ModList.get().isLoaded("ae2");
    private static final boolean MEK = ModList.get().isLoaded("mekanism");
    private static final boolean TOMS = ModList.get().isLoaded("toms_storage");
    private static final boolean SSN = ModList.get().isLoaded("storagenetwork");
    private static final boolean PIPES = ModList.get().isLoaded("prettypipes");

    @Override
    public boolean hasLootTable(RandomizableContainerBlockEntity be) {
        return be.getLootTable() != null;
    }

    @Override
    public boolean isLootr(BlockEntity be) {
        return LOOTR && LootrCompat.isLootrContainer(be);
    }

    @Override
    public boolean isLootr(AbstractMinecartContainer cart) {
        return LOOTR && LootrCompat.isLootrCart(cart);
    }

    @Override
    public int lootLootr(ServerPlayer player, BlockEntity be) {
        return LootrCompat.lootContainer(player, be);
    }

    @Override
    public int lootLootr(ServerPlayer player, AbstractMinecartContainer cart) {
        return LootrCompat.lootCart(player, cart);
    }

    @Override
    public void giveItem(ServerPlayer player, ItemStack stack) {
        ItemHandlerHelper.giveItemToPlayer(player, stack);
    }

    @Override
    public void sendFeedback(ServerPlayer player, Component message, Component transfer) {
        PacketDistributor.sendToPlayer(player, new LootFeedbackPacket(message, Optional.ofNullable(transfer)));
    }

    @Override
    public ResolvedSink blockSink(ServerPlayer player, ServerLevel level, BlockPos pos, boolean chunkReady) {
        if (RS) {
            LootSink rsSink = RefinedStorageCompat.blockSink(level, pos);
            if (rsSink != null) {
                return new ResolvedSink(rsSink, level.getBlockState(pos).getBlock().getName());
            }
        }
        if (!chunkReady) {
            return null;
        }
        Component blockName = level.getBlockState(pos).getBlock().getName();
        if (PE && ProjectECompat.isTransmutationTable(level.getBlockState(pos))) {
            LootSink emcSink = ProjectECompat.personalEmcSink(player);
            if (emcSink != null) {
                return new ResolvedSink(emcSink, blockName, () -> ProjectECompat.syncPersonal(player));
            }
        }
        if (AE2) {
            LootSink ae2Sink = AppliedEnergisticsCompat.blockSink(level, pos, player);
            if (ae2Sink != null) {
                return new ResolvedSink(ae2Sink, Text.translatable("name.lootall.me_network"));
            }
        }
        if (MEK) {
            LootSink mekSink = MekanismCompat.blockSink(level, pos);
            if (mekSink != null) {
                return new ResolvedSink(mekSink, blockName);
            }
        }
        if (TOMS) {
            LootSink tomsSink = TomsStorageCompat.blockSink(level, pos);
            if (tomsSink != null) {
                return new ResolvedSink(tomsSink, blockName);
            }
        }
        if (SSN) {
            LootSink ssnSink = SimpleStorageNetworkCompat.blockSink(level, pos);
            if (ssnSink != null) {
                return new ResolvedSink(ssnSink, blockName);
            }
        }
        if (PIPES) {
            LootSink pipesSink = PrettyPipesCompat.blockSink(level, pos);
            if (pipesSink != null) {
                return new ResolvedSink(pipesSink, blockName);
            }
        }
        BlockEntity be = level.getBlockEntity(pos);
        IItemHandler handler = be == null ? null : findHandler(be);
        if (handler == null) {
            return null;
        }
        return new ResolvedSink(stack -> ItemHandlerHelper.insertItemStacked(handler, stack, false), blockName);
    }

    @Override
    public ResolvedSink itemSink(ServerPlayer player, Item item) {
        if (RS) {
            ItemStack networkStack = findPlayerStack(player, item);
            if (networkStack != null && RefinedStorageCompat.isNetworkItem(networkStack)) {
                LootSink rsSink = RefinedStorageCompat.itemSink(player, networkStack);
                if (rsSink != null) {
                    return new ResolvedSink(rsSink, networkStack.getHoverName());
                }
            }
        }
        if (PE) {
            ItemStack holderStack = findPlayerStack(player, item);
            if (holderStack != null && ProjectECompat.isEmcHolder(holderStack)) {
                LootSink emcSink = ProjectECompat.emcSink(holderStack);
                if (emcSink != null) {
                    return new ResolvedSink(emcSink, holderStack.getHoverName());
                }
            }
            if (ProjectECompat.isTransmutationTablet(item)) {
                LootSink emcSink = ProjectECompat.personalEmcSink(player);
                if (emcSink != null) {
                    return new ResolvedSink(emcSink, new ItemStack(item).getHoverName(),
                            () -> ProjectECompat.syncPersonal(player));
                }
            }
        }
        if (MEK) {
            ItemStack qioStack = findPlayerStack(player, item);
            if (qioStack != null && MekanismCompat.isQioItem(qioStack)) {
                LootSink mekSink = MekanismCompat.itemSink(qioStack);
                if (mekSink != null) {
                    return new ResolvedSink(mekSink, qioStack.getHoverName());
                }
            }
        }
        IItemHandler handler = resolveItemHandler(player, item);
        if (handler == null) {
            return null;
        }
        return new ResolvedSink(stack -> ItemHandlerHelper.insertItemStacked(handler, stack, false),
                new ItemStack(item).getHoverName());
    }

    @Override
    public boolean canTargetBlock(ServerLevel level, BlockPos pos) {
        if (RS && RefinedStorageCompat.blockSink(level, pos) != null) {
            return true;
        }
        if (PE && ProjectECompat.isTransmutationTable(level.getBlockState(pos))) {
            return true;
        }
        if (AE2 && AppliedEnergisticsCompat.hasNetwork(level, pos)) {
            return true;
        }
        if (MEK && MekanismCompat.blockSink(level, pos) != null) {
            return true;
        }
        if (TOMS && TomsStorageCompat.blockSink(level, pos) != null) {
            return true;
        }
        if (SSN && SimpleStorageNetworkCompat.blockSink(level, pos) != null) {
            return true;
        }
        if (PIPES && PrettyPipesCompat.blockSink(level, pos) != null) {
            return true;
        }
        BlockEntity be = level.getBlockEntity(pos);
        return be != null && findHandler(be) != null;
    }

    @Override
    public boolean canTargetItem(ServerPlayer player, Item item) {
        if (RS) {
            ItemStack stack = findPlayerStack(player, item);
            if (stack != null && RefinedStorageCompat.isNetworkItem(stack)) {
                return true;
            }
        }
        if (PE) {
            ItemStack stack = findPlayerStack(player, item);
            if (stack != null && ProjectECompat.isEmcHolder(stack)) {
                return true;
            }
            if (ProjectECompat.isTransmutationTablet(item)) {
                return true;
            }
        }
        if (MEK) {
            ItemStack stack = findPlayerStack(player, item);
            if (stack != null && MekanismCompat.isQioItem(stack)) {
                return true;
            }
        }
        return resolveItemHandler(player, item) != null;
    }

    private static IItemHandler resolveItemHandler(ServerPlayer player, Item item) {
        IItemHandler handler = inventoryItemHandler(player, item);
        if (handler == null) {
            handler = CuriosCompat.findItemHandler(player, item);
        }
        return handler;
    }

    private static ItemStack findPlayerStack(ServerPlayer player, Item item) {
        Inventory inventory = Compat.inventory(player);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                return stack;
            }
        }
        return null;
    }

    private static IItemHandler inventoryItemHandler(ServerPlayer player, Item item) {
        Inventory inventory = Compat.inventory(player);
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (!stack.isEmpty() && stack.getItem() == item) {
                IItemHandler handler = stack.getCapability(Capabilities.ItemHandler.ITEM);
                if (handler != null) {
                    return handler;
                }
            }
        }
        return null;
    }

    private static IItemHandler findHandler(BlockEntity be) {
        Level level = be.getLevel();
        if (level == null) {
            return null;
        }
        BlockPos pos = be.getBlockPos();
        IItemHandler handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, null);
        if (handler != null) {
            return handler;
        }
        for (Direction direction : Direction.values()) {
            handler = level.getCapability(Capabilities.ItemHandler.BLOCK, pos, direction);
            if (handler != null) {
                return handler;
            }
        }
        return null;
    }
}
