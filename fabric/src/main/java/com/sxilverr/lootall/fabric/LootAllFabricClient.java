package com.sxilverr.lootall.fabric;

import com.sxilverr.lootall.client.ClientEvents;
import com.sxilverr.lootall.client.KeyBindings;
import com.sxilverr.lootall.client.TransferFeedback;
import com.sxilverr.lootall.network.LootAllNetworkClient;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;

public final class LootAllFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        KeyBindings.register();
        LootAllNetworkClient.registerClient();
        ClientEvents.register();
        HudRenderCallback.EVENT.register((graphics, tick) ->
                TransferFeedback.render(graphics, graphics.guiWidth(), graphics.guiHeight()));
    }
}
