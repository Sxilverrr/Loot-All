package com.sxilverr.lootall.client;

import com.sxilverr.lootall.config.LootConfig;
import com.sxilverr.lootall.network.LootAllNetworkClient;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;

public final class ClientEvents {

    private ClientEvents() {
    }

    public static void register() {
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (client.player == null) {
                return;
            }
            TransferFeedback.tick();
            while (KeyBindings.LOOT_ALL.consumeClick()) {
                LootAllNetworkClient.sendLootAll();
            }
            if (LootConfig.enableLootingTransfer && KeyBindings.SET_TRANSFER_TARGET != null) {
                while (KeyBindings.SET_TRANSFER_TARGET.consumeClick()) {
                    HitResult hit = client.hitResult;
                    if (hit instanceof BlockHitResult blockHit && hit.getType() == HitResult.Type.BLOCK) {
                        LootAllNetworkClient.sendSetBlockTarget(blockHit.getBlockPos());
                    } else {
                        LootAllNetworkClient.sendClearTarget();
                    }
                }
            }
        });
    }
}
