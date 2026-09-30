package com.sxilverr.lootall.server;

import com.sxilverr.lootall.Config;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.tick.ServerTickEvent;

@EventBusSubscriber(modid = Config.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void onServerTick(ServerTickEvent.Post event) {
        LootAllHandler.tick(event.getServer());
    }
}
