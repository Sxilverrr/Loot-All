package com.sxilverr.lootall.fabric;

import com.sxilverr.lootall.Config;
import com.sxilverr.lootall.network.LootAllNetwork;
import com.sxilverr.lootall.platform.LootPlatform;
import com.sxilverr.lootall.server.LootAllHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public final class LootAllFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        LootPlatform.INSTANCE = new FabricPlatform();
        Config.load();
        LootAllNetwork.registerServer();
        ServerTickEvents.END_SERVER_TICK.register(LootAllHandler::tick);
    }
}
