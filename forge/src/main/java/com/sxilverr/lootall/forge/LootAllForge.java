package com.sxilverr.lootall.forge;

import com.sxilverr.lootall.Config;
import com.sxilverr.lootall.network.LootAllNetwork;
import com.sxilverr.lootall.platform.LootPlatform;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

@Mod(Config.MOD_ID)
public final class LootAllForge {

    public LootAllForge() {
        LootPlatform.INSTANCE = new ForgePlatform();
        LootAllNetwork.register();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }
}
