package com.sxilverr.lootall.neoforge;

import com.sxilverr.lootall.Config;
import com.sxilverr.lootall.platform.LootPlatform;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;

@Mod(Config.MOD_ID)
public final class LootAllNeoForge {

    public LootAllNeoForge(ModContainer container) {
        LootPlatform.INSTANCE = new NeoForgePlatform();
        container.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
