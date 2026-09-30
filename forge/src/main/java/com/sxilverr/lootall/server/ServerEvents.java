package com.sxilverr.lootall.server;

import com.sxilverr.lootall.Config;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
//? if >=1.17 {
import net.minecraftforge.server.ServerLifecycleHooks;
//?} else {
/*import net.minecraftforge.fml.server.ServerLifecycleHooks;*/
//?}

@Mod.EventBusSubscriber(modid = Config.MOD_ID)
public class ServerEvents {
    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (event.phase == TickEvent.Phase.END && server != null) {
            LootAllHandler.tick(server);
        }
    }
}
